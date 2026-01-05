package com.plantmemory.app.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.plantmemory.app.MainActivity
import com.plantmemory.app.PlantMemoryApplication
import com.plantmemory.app.R
import com.plantmemory.app.data.JournalEntry
import com.plantmemory.app.ui.components.PlantResources
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import kotlin.math.ceil

/**
 * 4x4 Plant Memory Garden Widget.
 * Shows ALL plants for the year in a grid format.
 * Tries current year first, falls back to last year if empty.
 * Bottom shows year and plant count.
 */
class PlantMemoryWidget : GlanceAppWidget() {
    
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = try {
            PlantMemoryApplication.instance.repository
        } catch (e: Exception) {
            null
        }
        
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        
        // Try current year first - get ALL entries (up to 366 for leap year)
        var yearEntries = repository?.let { 
            runBlocking { it.getEntriesByYearSync(currentYear, 366) }
        } ?: emptyList()
        
        var displayYear = currentYear
        
        // If current year is empty, try last year
        if (yearEntries.isEmpty()) {
            val lastYear = currentYear - 1
            yearEntries = repository?.let { 
                runBlocking { it.getEntriesByYearSync(lastYear, 366) }
            } ?: emptyList()
            
            if (yearEntries.isNotEmpty()) {
                displayYear = lastYear
            }
        }
        
        provideContent {
            GlanceTheme {
                WidgetContent(
                    context = context,
                    entries = yearEntries,
                    year = displayYear
                )
            }
        }
    }
    
    @Composable
    private fun WidgetContent(
        context: Context,
        entries: List<JournalEntry>,
        year: Int
    ) {
        // Create intent to open home screen and show latest memory
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_SHOW_LATEST_MEMORY, true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(R.color.widget_background))
                .cornerRadius(24.dp)
                .clickable(actionStartActivity(intent))
                .padding(12.dp)
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Plant grid - takes most of the space
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .defaultWeight(),
                    contentAlignment = Alignment.Center
                ) {
                    if (entries.isEmpty()) {
                        Text(
                            text = "Start your garden",
                            style = TextStyle(
                                color = ColorProvider(R.color.text_muted),
                                fontSize = 12.sp
                            )
                        )
                    } else {
                        // Show ALL plants in a responsive grid
                        AllPlantsGrid(entries = entries)
                    }
                }
                
                Spacer(modifier = GlanceModifier.height(4.dp))
                
                // Bottom bar with year and plant count
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = year.toString(),
                        style = TextStyle(
                            color = ColorProvider(R.color.text_muted),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    
                    Text(
                        text = "${entries.size} plants",
                        style = TextStyle(
                            color = ColorProvider(R.color.text_muted),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
    
    @Composable
    private fun AllPlantsGrid(entries: List<JournalEntry>) {
        // Calculate optimal grid size based on entry count
        // For 4x4 widget, we have roughly 120dp x 120dp usable space
        // Adjust icon size and columns based on count
        val count = entries.size
        val (columns, iconSize) = when {
            count <= 9 -> Pair(3, 28.dp)      // 3x3 grid, larger icons
            count <= 16 -> Pair(4, 24.dp)     // 4x4 grid
            count <= 25 -> Pair(5, 20.dp)     // 5x5 grid
            count <= 36 -> Pair(6, 16.dp)     // 6x6 grid
            count <= 49 -> Pair(7, 14.dp)     // 7x7 grid
            count <= 64 -> Pair(8, 12.dp)     // 8x8 grid
            count <= 81 -> Pair(9, 10.dp)     // 9x9 grid
            count <= 100 -> Pair(10, 9.dp)    // 10x10 grid
            count <= 144 -> Pair(12, 7.dp)    // 12x12 grid
            count <= 196 -> Pair(14, 6.dp)    // 14x14 grid
            count <= 256 -> Pair(16, 5.dp)    // 16x16 grid
            else -> Pair(19, 4.dp)            // 19 columns for 365 days
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            entries.chunked(columns).forEach { row ->
                Row {
                    row.forEach { entry ->
                        Image(
                            provider = ImageProvider(
                                PlantResources.getPlantDrawable(entry.plantType, entry.plantVariant)
                            ),
                            contentDescription = null,
                            modifier = GlanceModifier.size(iconSize)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Receiver for the 4x4 widget.
 */
class PlantMemoryWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = PlantMemoryWidget()
}
