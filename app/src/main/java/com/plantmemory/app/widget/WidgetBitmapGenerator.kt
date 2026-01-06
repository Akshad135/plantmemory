package com.plantmemory.app.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import com.plantmemory.app.data.JournalEntry
import com.plantmemory.app.ui.components.PlantResources
import kotlin.math.ceil
import kotlin.math.sqrt

/**
 * Helper to generate a single bitmap containing the entire plant grid.
 * This bypasses RemoteViews/Glance limitations on view counts (limiting strictly to 1 view).
 */
object WidgetBitmapGenerator {

    /**
     * Draws the grid of plants onto a single Bitmap.
     */
    fun generateGridBitmap(
        context: Context,
        entries: List<JournalEntry>,
        widthPx: Int = 400,  // Reduced to 400 to stay under IPC limit (1MB)
        heightPx: Int = 400  // 400x400x4 = 640KB (Safe)
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        if (entries.isEmpty()) return bitmap

        val count = entries.size
        
        // Calculate grid (approx square)
        // For 365 items -> 20 cols x 19 rows
        val columns = ceil(sqrt(count.toDouble())).toInt().coerceAtLeast(1)
        val rows = ceil(count.toDouble() / columns).toInt().coerceAtLeast(1)
        
        val cellWidth = widthPx / columns.toFloat()
        val cellHeight = heightPx / rows.toFloat()
        
        // Scale icon to fit cell with some padding (10%)
        val padding = 0.1f
        val iconSize = (minOf(cellWidth, cellHeight) * (1 - padding)).toInt()
        
        var currentX = 0f
        var currentY = 0f
        
        entries.forEachIndexed { index, entry ->
            val col = index % columns
            val row = index / columns
            
            // Calculate position center
            val centerX = (col * cellWidth) + (cellWidth / 2)
            val centerY = (row * cellHeight) + (cellHeight / 2)
            
            // Get drawable
            val resId = PlantResources.getPlantDrawable(entry.plantType, entry.plantVariant)
            val drawable = ContextCompat.getDrawable(context, resId)
            
            drawable?.let {
                // Tint to black/white if needed (assuming icons are vector black)
                // If they are colored, no tint needed. The app uses black icons.
                // But widget background is colored, so check theme.
                // Assuming default is correct color from drawable.
                
                // Bounds
                val left = (centerX - iconSize / 2).toInt()
                val top = (centerY - iconSize / 2).toInt()
                val right = left + iconSize
                val bottom = top + iconSize
                
                it.setBounds(left, top, right, bottom)
                it.draw(canvas)
            }
        }
        
        return bitmap
    }
}
