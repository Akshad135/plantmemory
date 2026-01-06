package com.plantmemory.app.data

/**
 * Enum representing all available icon types for journal entries.
 * Expanded from original 5 plant types to 32 diverse doodle icons.
 */
enum class PlantType {
    // Original plant types (kept for backwards compatibility)
    SIMPLE,
    TREE,
    CLUSTER,
    GRASS,
    MUSHROOM,
    
    // Flowers
    SUNFLOWER,
    TULIP,
    ROSE,
    DAISY,
    
    // Plants
    CACTUS,
    LEAF,
    SEEDLING,
    CLOVER,
    FERN,
    
    // Creatures
    BUTTERFLY,
    BEE,
    LADYBUG,
    SNAIL,
    BIRD,
    CAT,
    
    // Weather/Sky
    SUN,
    MOON,
    STAR,
    CLOUD,
    RAINBOW,
    RAINDROP,
    
    // Garden/Nature
    WATERING_CAN,
    POT,
    ACORN,
    PINECONE,
    BIRDHOUSE,
    
    // Fruits
    APPLE,
    CHERRY,
    
    // Misc
    HEART,
    SPARKLE,
    FEATHER,
    SHELL;
    
    companion object {
        fun fromOrdinal(ordinal: Int): PlantType {
            return entries.getOrElse(ordinal) { SIMPLE }
        }
        
        /**
         * Get the new expanded icon types (excluding original 5 for selector display)
         */
        fun getExpandedTypes(): List<PlantType> = entries.toList()
    }
}
