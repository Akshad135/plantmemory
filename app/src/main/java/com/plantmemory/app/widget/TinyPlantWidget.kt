package com.plantmemory.app.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
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
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.unit.ColorProvider
import com.plantmemory.app.MainActivity
import com.plantmemory.app.PlantMemoryApplication
import com.plantmemory.app.R
import com.plantmemory.app.ui.components.PlantResources
import kotlinx.coroutines.runBlocking

/**
 * Material 3 styled 1x1 Tiny Plant Widget.
 * Shows just the latest plant icon in a container.
 */
class TinyPlantWidget : GlanceAppWidget() {
    
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = try {
            PlantMemoryApplication.instance.repository
        } catch (e: Exception) {
            null
        }
        
        val latestEntry = repository?.let { 
            runBlocking { it.getRecentEntries(1) }
        }?.firstOrNull()
        
        val plantDrawable = latestEntry?.let {
            PlantResources.getPlantDrawable(it.plantType, it.plantVariant)
        } ?: R.drawable.plant_simple_1
        
        provideContent {
            GlanceTheme {
                TinyWidgetContent(context = context, plantDrawable = plantDrawable)
            }
        }
    }
    
    @Composable
    private fun TinyWidgetContent(context: Context, plantDrawable: Int) {
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
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            // Icon container with accent background
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(ColorProvider(R.color.indigo_container))
                    .cornerRadius(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    provider = ImageProvider(plantDrawable),
                    contentDescription = "Latest plant",
                    modifier = GlanceModifier.size(36.dp)
                )
            }
        }
    }
}

/**
 * Receiver for the 1x1 widget.
 */
class TinyPlantWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TinyPlantWidget()
}
