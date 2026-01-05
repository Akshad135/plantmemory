package com.plantmemory.app.data

import androidx.room.TypeConverter

/**
 * Type converters for Room to handle custom types.
 */
class Converters {
    
    @TypeConverter
    fun fromPlantType(plantType: PlantType): Int {
        return plantType.ordinal
    }
    
    @TypeConverter
    fun toPlantType(ordinal: Int): PlantType {
        return PlantType.fromOrdinal(ordinal)
    }
}
