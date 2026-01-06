package com.plantmemory.app.ui.components

import com.plantmemory.app.R
import com.plantmemory.app.data.PlantType

/**
 * Utility object for managing plant/icon drawable resources.
 * Supports both original plant variants and new single-icon types.
 */
object PlantResources {
    
    // Original plant types with 8 variants each
    private val simplePlants = listOf(
        R.drawable.plant_simple_1, R.drawable.plant_simple_2, R.drawable.plant_simple_3, R.drawable.plant_simple_4,
        R.drawable.plant_simple_5, R.drawable.plant_simple_6, R.drawable.plant_simple_7, R.drawable.plant_simple_8
    )
    private val treePlants = listOf(
        R.drawable.plant_tree_1, R.drawable.plant_tree_2, R.drawable.plant_tree_3, R.drawable.plant_tree_4,
        R.drawable.plant_tree_5, R.drawable.plant_tree_6, R.drawable.plant_tree_7, R.drawable.plant_tree_8
    )
    private val clusterPlants = listOf(
        R.drawable.plant_cluster_1, R.drawable.plant_cluster_2, R.drawable.plant_cluster_3, R.drawable.plant_cluster_4,
        R.drawable.plant_cluster_5, R.drawable.plant_cluster_6, R.drawable.plant_cluster_7, R.drawable.plant_cluster_8
    )
    private val grassPlants = listOf(
        R.drawable.plant_grass_1, R.drawable.plant_grass_2, R.drawable.plant_grass_3, R.drawable.plant_grass_4,
        R.drawable.plant_grass_5, R.drawable.plant_grass_6, R.drawable.plant_grass_7, R.drawable.plant_grass_8
    )
    private val mushroomPlants = listOf(
        R.drawable.plant_mushroom_1, R.drawable.plant_mushroom_2, R.drawable.plant_mushroom_3, R.drawable.plant_mushroom_4,
        R.drawable.plant_mushroom_5, R.drawable.plant_mushroom_6, R.drawable.plant_mushroom_7, R.drawable.plant_mushroom_8
    )
    
    // New single-icon types (no variants)
    private val singleIcons = mapOf(
        PlantType.SUNFLOWER to R.drawable.icon_sunflower,
        PlantType.TULIP to R.drawable.icon_tulip,
        PlantType.ROSE to R.drawable.icon_rose,
        PlantType.DAISY to R.drawable.icon_daisy,
        PlantType.CACTUS to R.drawable.icon_cactus,
        PlantType.LEAF to R.drawable.icon_leaf,
        PlantType.SEEDLING to R.drawable.icon_seedling,
        PlantType.CLOVER to R.drawable.icon_clover,
        PlantType.FERN to R.drawable.icon_fern,
        PlantType.BUTTERFLY to R.drawable.icon_butterfly,
        PlantType.BEE to R.drawable.icon_bee,
        PlantType.LADYBUG to R.drawable.icon_ladybug,
        PlantType.SNAIL to R.drawable.icon_snail,
        PlantType.BIRD to R.drawable.icon_bird,
        PlantType.CAT to R.drawable.icon_cat,
        PlantType.SUN to R.drawable.icon_sun,
        PlantType.MOON to R.drawable.icon_moon,
        PlantType.STAR to R.drawable.icon_star,
        PlantType.CLOUD to R.drawable.icon_cloud,
        PlantType.RAINBOW to R.drawable.icon_rainbow,
        PlantType.RAINDROP to R.drawable.icon_raindrop,
        PlantType.WATERING_CAN to R.drawable.icon_watering_can,
        PlantType.POT to R.drawable.icon_pot,
        PlantType.ACORN to R.drawable.icon_acorn,
        PlantType.PINECONE to R.drawable.icon_pinecone,
        PlantType.BIRDHOUSE to R.drawable.icon_birdhouse,
        PlantType.APPLE to R.drawable.icon_apple,
        PlantType.CHERRY to R.drawable.icon_cherry,
        PlantType.HEART to R.drawable.icon_heart,
        PlantType.SPARKLE to R.drawable.icon_sparkle,
        PlantType.FEATHER to R.drawable.icon_feather,
        PlantType.SHELL to R.drawable.icon_shell
    )
    
    /**
     * Get the drawable resource for a specific plant type and variant.
     * For new icon types, variant is ignored (single icon per type).
     */
    fun getPlantDrawable(plantType: PlantType, variant: Int): Int {
        // Check if it's a new single-icon type
        singleIcons[plantType]?.let { return it }
        
        // Otherwise use original variant-based system
        val plants = when (plantType) {
            PlantType.SIMPLE -> simplePlants
            PlantType.TREE -> treePlants
            PlantType.CLUSTER -> clusterPlants
            PlantType.GRASS -> grassPlants
            PlantType.MUSHROOM -> mushroomPlants
            else -> simplePlants // Fallback
        }
        val index = (variant - 1).coerceIn(0, plants.lastIndex)
        return plants[index]
    }
    
    /**
     * Get a representative drawable for a plant type (for the selector).
     */
    fun getTypeSelectorDrawable(plantType: PlantType): Int {
        // For new types, just return the single icon
        singleIcons[plantType]?.let { return it }
        
        // For original types, return a representative variant
        return when (plantType) {
            PlantType.SIMPLE -> R.drawable.plant_simple_1
            PlantType.TREE -> R.drawable.plant_tree_3
            PlantType.CLUSTER -> R.drawable.plant_cluster_2
            PlantType.GRASS -> R.drawable.plant_grass_1
            PlantType.MUSHROOM -> R.drawable.plant_mushroom_1
            else -> R.drawable.plant_simple_1
        }
    }
    
    /**
     * Get all plant types in display order.
     */
    fun getAllTypes(): List<PlantType> = PlantType.entries
}
