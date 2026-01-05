package com.plantmemory.app.data

/**
 * Enum representing the different plant categories/types
 * that users can select when creating a journal entry.
 */
enum class PlantType {
    SIMPLE,    // Basic stems and single flowers
    TREE,      // Bushes and tree shapes
    CLUSTER,   // Berry clusters and grouped flowers
    GRASS,     // Tall reeds and grasses
    MUSHROOM;  // Mushrooms and succulents
    
    companion object {
        fun fromOrdinal(ordinal: Int): PlantType {
            return entries.getOrElse(ordinal) { SIMPLE }
        }
    }
}
