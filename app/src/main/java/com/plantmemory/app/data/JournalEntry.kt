package com.plantmemory.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a single journal entry in the Plant Memory app.
 * Each entry has associated text content and is visualized as a plant doodle.
 */
@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /** The journal text content */
    val text: String,
    
    /** Timestamp when the entry was created (epoch millis) */
    val timestamp: Long,
    
    /** The type/category of plant to display */
    val plantType: PlantType,
    
    /** Variant within the plant type (1-8) for visual variety */
    val plantVariant: Int,
    
    /** X position in the garden grid (0.0 to 1.0) */
    val gridX: Float = 0f,
    
    /** Y position within the day cluster */
    val gridY: Float = 0f
)
