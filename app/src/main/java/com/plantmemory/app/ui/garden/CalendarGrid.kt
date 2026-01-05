package com.plantmemory.app.ui.garden

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
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

/**
 * Data class for a calendar day cell.
 */
data class CalendarDay(
    val dayOfYear: Int,
    val dayOfMonth: Int,
    val month: Int,
    val year: Int,
    val entry: JournalEntry? = null
)

/**
 * Unified 365-day grid that fits on screen without scrolling.
 * Uses Canvas for empty dots + positioned Images for plants.
 * 
 * Scaling logic:
 * - Full garden (365): Plants fill ~90% of cell, tightly packed like reference
 * - Medium garden (100-200): Plants fill ~100-110% of cell, slight overlap
 * - Small garden (<50): Plants fill 150-200% of cell, very prominent
 * - Dots are inverse: tiny when few entries, more visible when full
 */
@Composable
fun CalendarGrid(
    entries: List<JournalEntry>,
    year: Int,
    onDayClick: (CalendarDay) -> Unit,
    modifier: Modifier = Modifier
) {
    // Build calendar days for the year
    val calendarDays = remember(year, entries) {
        buildCalendarDays(year, entries)
    }
    
    // Filter only days with entries for rendering plants
    val daysWithEntries = remember(calendarDays) {
        calendarDays.filter { it.entry != null }
    }
    
    // Grid dimensions: 19 columns fits 365 days nicely
    val columns = 19
    val totalDays = calendarDays.size
    val rows = ceil(totalDays.toFloat() / columns).toInt()
    
    // Dynamic sizing based on entry count
    val entryCount = entries.size
    
    // Plant size relative to cell: 
    // - Full garden: 0.85 (tight fit like reference image)
    // - Medium: slightly larger
    // - Few entries: much larger to stand out
    val plantSizeRatio = when {
        entryCount >= 300 -> 0.85f   // Full garden - tight fit
        entryCount >= 200 -> 0.90f   // Almost full
        entryCount >= 100 -> 1.0f    // Medium - fill cell
        entryCount >= 50 -> 1.2f     // Half full - slightly overlap
        entryCount >= 20 -> 1.5f     // Few entries - prominent
        entryCount >= 5 -> 2.0f      // Very few - very large
        else -> 2.5f                  // 1-4 entries - huge
    }
    
    // Dot visibility: almost invisible when few entries, more visible when full
    val dotAlpha = when {
        entryCount >= 300 -> 0.15f
        entryCount >= 200 -> 0.12f
        entryCount >= 100 -> 0.10f
        entryCount >= 50 -> 0.08f
        else -> 0.05f                 // Few entries - barely visible dots
    }
    
    val dotSizeRatio = when {
        entryCount >= 300 -> 0.12f   // Full - slightly visible
        entryCount >= 200 -> 0.10f
        entryCount >= 100 -> 0.08f
        entryCount >= 50 -> 0.06f
        else -> 0.04f                 // Few entries - tiny dots
    }
    
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val cellWidthPx = constraints.maxWidth.toFloat() / columns
        val cellHeightPx = constraints.maxHeight.toFloat() / rows
        val cellSizePx = minOf(cellWidthPx, cellHeightPx)
        
        val plantSizePx = cellSizePx * plantSizeRatio
        val plantSizeDp = with(density) { plantSizePx.toDp() }
        val dotRadius = cellSizePx * dotSizeRatio
        
        // Draw dots for all empty days using Canvas (fast)
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
        
        // Render plants as positioned Images (only for days with entries)
        daysWithEntries.forEach { day ->
            val index = day.dayOfYear - 1 // dayOfYear is 1-indexed
            val col = index % columns
            val row = index / columns
            // Center the plant in the cell (may overflow if size > cell)
            val offsetX = (col * cellWidthPx + (cellWidthPx - plantSizePx) / 2).roundToInt()
            val offsetY = (row * cellHeightPx + (cellHeightPx - plantSizePx) / 2).roundToInt()
            
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
                        .size(plantSizeDp),
                    colorFilter = ColorFilter.tint(IndigoPrimary)
                )
            }
        }
    }
}

/**
 * Build calendar days for a year, matching entries to their dates.
 */
private fun buildCalendarDays(year: Int, entries: List<JournalEntry>): List<CalendarDay> {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, Calendar.JANUARY)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    
    // Map entries by date string for O(1) lookup
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
