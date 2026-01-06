package com.plantmemory.app.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
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

/**
 * Material 3 styled 4x4 Plant Memory Garden Widget.
 * Shows ALL plants for the year in a dynamic grid.
 * Refined design with header badge and plant count.
 */
class PlantMemoryWidget : GlanceAppWidget() {
    
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = try {
            PlantMemoryApplication.instance.repository
        } catch (e: Exception) {
            null
        }
        
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        
        var yearEntries = repository?.let { 
            runBlocking { it.getEntriesByYearSync(currentYear, 366) }
        } ?: emptyList()
        
        var displayYear = currentYear
        
        // Fallback to last year if empty
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
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_SHOW_LATEST_MEMORY, true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(R.color.widget_background))
                .cornerRadius(28.dp)
                .clickable(actionStartActivity(intent))
                .padding(14.dp)
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with year badge and count
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Year badge
                    Box(
                        modifier = GlanceModifier
                            .background(ColorProvider(R.color.indigo_container))
                            .cornerRadius(8.dp)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = year.toString(),
                            style = TextStyle(
                                color = ColorProvider(R.color.indigo_primary),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    
                    Text(
                        text = "${entries.size} memories",
                        style = TextStyle(
                            color = ColorProvider(R.color.text_muted),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                
                Spacer(modifier = GlanceModifier.height(8.dp))
                
                // Plant grid
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .defaultWeight()
                        .background(ColorProvider(R.color.surface_variant))
                        .cornerRadius(16.dp)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (entries.isEmpty()) {
                        Text(
                            text = "tap to add your first memory",
                            style = TextStyle(
                                color = ColorProvider(R.color.text_muted),
                                fontSize = 11.sp
                            )
                        )
                    } else {
                        AllPlantsGrid(context = context, entries = entries)
                    }
                }
            }
        }
    }
    
    @Composable
    private fun AllPlantsGrid(context: Context, entries: List<JournalEntry>) {
        // Use a single generated bitmap to display all icons.
        // This bypasses RemoteViews limits and guarantees all 365+ icons are shown.
        val bitmap = WidgetBitmapGenerator.generateGridBitmap(context, entries)
        
        // Display as a single image filling the space
        Image(
            provider = ImageProvider(bitmap),
            contentDescription = "Garden Grid",
            contentScale = androidx.glance.layout.ContentScale.Fit,
            modifier = GlanceModifier.fillMaxSize()
        )
    }
}

/**
 * Receiver for the 4x4 widget.
 */
class PlantMemoryWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = PlantMemoryWidget()
}
