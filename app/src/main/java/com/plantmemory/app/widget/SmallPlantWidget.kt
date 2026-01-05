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
 * 2x2 Small Plant Widget.
 * Shows latest plant, memory text snippet, day name, and date of latest entry.
 * Opens to home screen showing latest memory when clicked.
 */
class SmallPlantWidget : GlanceAppWidget() {
    
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = try {
            PlantMemoryApplication.instance.repository
        } catch (e: Exception) {
            null
        }
        
        // Get latest entry by timestamp (most recent recorded)
        val recentEntries = repository?.let { 
            runBlocking { it.getRecentEntries(1) }
        } ?: emptyList()
        val latestEntry = recentEntries.firstOrNull()
        
        // Format date of the latest entry
        val dayFormat = SimpleDateFormat("EEEE", Locale.US)
        val dateFormat = SimpleDateFormat("MM.dd", Locale.US)
        val entryDate = latestEntry?.let { Date(it.timestamp) } ?: Date()
        val dayOfWeek = dayFormat.format(entryDate).lowercase()
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
        // Create intent to open home screen and show latest memory
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_SHOW_LATEST_MEMORY, true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(R.color.widget_background))
                .cornerRadius(20.dp)
                .clickable(actionStartActivity(intent))
                .padding(12.dp)
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top: day name and date
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dayOfWeek,
                        style = TextStyle(
                            color = ColorProvider(R.color.indigo_primary),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    
                    Text(
                        text = date,
                        style = TextStyle(
                            color = ColorProvider(R.color.text_muted),
                            fontSize = 11.sp
                        )
                    )
                }
                
                Spacer(modifier = GlanceModifier.height(4.dp))
                
                // Center: Plant icon
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .defaultWeight(),
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
                            modifier = GlanceModifier.size(48.dp)
                        )
                    } else {
                        Image(
                            provider = ImageProvider(R.drawable.plant_simple_1),
                            contentDescription = "Plant",
                            modifier = GlanceModifier.size(48.dp)
                        )
                    }
                }
                
                Spacer(modifier = GlanceModifier.height(4.dp))
                
                // Bottom: Memory text snippet
                val memoryText = latestEntry?.text?.take(40) ?: "plant memory"
                val displayText = if ((latestEntry?.text?.length ?: 0) > 40) "$memoryText..." else memoryText
                
                Text(
                    text = displayText,
                    style = TextStyle(
                        color = ColorProvider(R.color.text_primary),
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 2
                )
            }
        }
    }
}

/**
 * Receiver for the small 2x2 widget.
 */
class SmallPlantWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SmallPlantWidget()
}
