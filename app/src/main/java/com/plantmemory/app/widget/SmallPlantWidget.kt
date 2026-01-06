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
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.plantmemory.app.MainActivity
import com.plantmemory.app.PlantMemoryApplication
import com.plantmemory.app.R
import com.plantmemory.app.data.JournalEntry
import com.plantmemory.app.ui.components.PlantResources
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Material 3 styled 2x2 Small Plant Widget.
 * Shows latest plant, memory text snippet, day name, and date.
 * Opens to garden screen with latest memory when clicked.
 */
class SmallPlantWidget : GlanceAppWidget() {
    
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = try {
            PlantMemoryApplication.instance.repository
        } catch (e: Exception) {
            null
        }
        
        // Get latest entry
        val recentEntries = repository?.let { 
            runBlocking { it.getRecentEntries(1) }
        } ?: emptyList()
        val latestEntry = recentEntries.firstOrNull()
        
        // Format date
        val dayFormat = SimpleDateFormat("EEE", Locale.US)
        val dateFormat = SimpleDateFormat("MMM d", Locale.US)
        val entryDate = latestEntry?.let { Date(it.timestamp) } ?: Date()
        val dayOfWeek = dayFormat.format(entryDate)
        val date = dateFormat.format(entryDate)
        
        provideContent {
            GlanceTheme {
                SmallWidgetContent(
                    context = context,
                    latestEntry = latestEntry,
                    dayOfWeek = dayOfWeek,
                    date = date
                )
            }
        }
    }
    
    @Composable
    private fun SmallWidgetContent(
        context: Context,
        latestEntry: JournalEntry?,
        dayOfWeek: String,
        date: String
    ) {
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
                .padding(14.dp)
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start
            ) {
                // Top: Date badge
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Day chip with accent background
                    Box(
                        modifier = GlanceModifier
                            .background(ColorProvider(R.color.indigo_container))
                            .cornerRadius(8.dp)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = dayOfWeek,
                            style = TextStyle(
                                color = ColorProvider(R.color.indigo_primary),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    
                    Spacer(modifier = GlanceModifier.width(6.dp))
                    
                    Text(
                        text = date,
                        style = TextStyle(
                            color = ColorProvider(R.color.text_muted),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                
                // Center: Plant icon in container
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .defaultWeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = GlanceModifier
                            .size(56.dp)
                            .background(ColorProvider(R.color.indigo_container))
                            .cornerRadius(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (latestEntry != null) {
                            Image(
                                provider = ImageProvider(
                                    PlantResources.getPlantDrawable(
                                        latestEntry.plantType,
                                        latestEntry.plantVariant
                                    )
                                ),
                                contentDescription = "Latest plant",
                                modifier = GlanceModifier.size(36.dp)
                            )
                        } else {
                            Image(
                                provider = ImageProvider(R.drawable.plant_simple_1),
                                contentDescription = "Plant",
                                modifier = GlanceModifier.size(36.dp)
                            )
                        }
                    }
                }
                
                // Bottom: Memory text
                val memoryText = latestEntry?.text?.take(50) ?: "tap to add memory"
                val displayText = if ((latestEntry?.text?.length ?: 0) > 50) "$memoryText..." else memoryText
                
                Text(
                    text = displayText,
                    style = TextStyle(
                        color = ColorProvider(R.color.text_primary),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Start
                    ),
                    maxLines = 2
                )
            }
        }
    }
}

/**
 * Receiver for the 2x2 widget.
 */
class SmallPlantWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SmallPlantWidget()
}
