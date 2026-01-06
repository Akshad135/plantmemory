package com.plantmemory.app.ui.garden

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import com.plantmemory.app.data.JournalEntry
import com.plantmemory.app.ui.components.PlantResources
import com.plantmemory.app.ui.theme.IndigoPrimary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.roundToInt

data class CalendarDay(
    val dayOfYear: Int,
    val dayOfMonth: Int,
    val month: Int,
    val year: Int,
    val entry: JournalEntry? = null
)

/**
 * 365-day grid like the reference:
 * - Icons large and clumped together
 * - Dots tiny and faded for empty days
 */
@Composable
fun CalendarGrid(
    entries: List<JournalEntry>,
    year: Int,
    onDayClick: (CalendarDay) -> Unit,
    modifier: Modifier = Modifier
) {
    val calendarDays = remember(year, entries) {
        buildCalendarDays(year, entries)
    }
    
    val daysWithEntries = remember(calendarDays) {
        calendarDays.filter { it.entry != null }
    }
    
    // Grid: 19 columns
    val columns = 19
    val totalDays = calendarDays.size
    val rows = ceil(totalDays.toFloat() / columns).toInt()
    
    val entryCount = entries.size
    
    // Subtle scaling based on entry count
    // Few entries = slightly larger icons, many = slightly smaller (still clumped)
    val iconSizeRatio = when {
        entryCount >= 300 -> 1.2f    // Full garden
        entryCount >= 200 -> 1.25f
        entryCount >= 100 -> 1.3f
        entryCount >= 50 -> 1.35f
        entryCount >= 20 -> 1.4f
        entryCount >= 5 -> 1.5f
        else -> 1.6f                  // Very few - slightly larger
    }
    
    val dotAlpha = 0.12f
    val dotSizeRatio = 0.04f
    
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val density = LocalDensity.current
        
        val cellWidthPx = constraints.maxWidth.toFloat() / columns
        val cellHeightPx = constraints.maxHeight.toFloat() / rows
        val cellSizePx = minOf(cellWidthPx, cellHeightPx)
        
        val iconSizePx = cellSizePx * iconSizeRatio
        val iconSizeDp = with(density) { iconSizePx.toDp() }
        val dotRadius = cellSizePx * dotSizeRatio
        
        // Draw tiny faded dots for empty days
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(calendarDays) {
                    detectTapGestures { offset ->
                        val col = (offset.x / cellWidthPx).toInt().coerceIn(0, columns - 1)
                        val row = (offset.y / cellHeightPx).toInt().coerceIn(0, rows - 1)
                        val dayIndex = row * columns + col
                        if (dayIndex < calendarDays.size) {
                            val day = calendarDays[dayIndex]
                            if (day.entry != null) {
                                onDayClick(day)
                            }
                        }
                    }
                }
        ) {
            calendarDays.forEachIndexed { index, day ->
                if (day.entry == null) {
                    val col = index % columns
                    val row = index / columns
                    val centerX = col * cellWidthPx + cellWidthPx / 2
                    val centerY = row * cellHeightPx + cellHeightPx / 2
                    
                    drawCircle(
                        color = IndigoPrimary.copy(alpha = dotAlpha),
                        radius = dotRadius,
                        center = Offset(centerX, centerY)
                    )
                }
            }
        }
        
        // Large icons clumped together
        daysWithEntries.forEach { day ->
            val index = day.dayOfYear - 1
            val col = index % columns
            val row = index / columns
            val offsetX = (col * cellWidthPx + (cellWidthPx - iconSizePx) / 2).roundToInt()
            val offsetY = (row * cellHeightPx + (cellHeightPx - iconSizePx) / 2).roundToInt()
            
            key(day.dayOfYear) {
                Image(
                    painter = painterResource(
                        id = PlantResources.getPlantDrawable(
                            day.entry!!.plantType,
                            day.entry.plantVariant
                        )
                    ),
                    contentDescription = "Memory on day ${day.dayOfYear}",
                    modifier = Modifier
                        .offset { IntOffset(offsetX, offsetY) }
                        .size(iconSizeDp),
                    colorFilter = ColorFilter.tint(IndigoPrimary)
                )
            }
        }
    }
}

private fun buildCalendarDays(year: Int, entries: List<JournalEntry>): List<CalendarDay> {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, Calendar.JANUARY)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    
    val entriesByDate = entries.associateBy { entry ->
        dateFormat.format(Date(entry.timestamp))
    }
    
    val days = mutableListOf<CalendarDay>()
    var currentYear = year
    
    while (currentYear == year) {
        val dateString = dateFormat.format(calendar.time)
        val entry = entriesByDate[dateString]
        
        days.add(
            CalendarDay(
                dayOfYear = calendar.get(Calendar.DAY_OF_YEAR),
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH),
                month = calendar.get(Calendar.MONTH),
                year = year,
                entry = entry
            )
        )
        
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        currentYear = calendar.get(Calendar.YEAR)
    }
    
    return days
}
