package com.plantmemory.app.ui.components

import com.plantmemory.app.R
import com.plantmemory.app.data.PlantType

/**
 * Utility object for managing plant drawable resources.
 */
object PlantResources {
    
    private val simplePlants = listOf(
        R.drawable.plant_simple_1,
        R.drawable.plant_simple_2,
        R.drawable.plant_simple_3,
        R.drawable.plant_simple_4,
        R.drawable.plant_simple_5,
        R.drawable.plant_simple_6,
        R.drawable.plant_simple_7,
        R.drawable.plant_simple_8
    )
    
    private val treePlants = listOf(
        R.drawable.plant_tree_1,
        R.drawable.plant_tree_2,
        R.drawable.plant_tree_3,
        R.drawable.plant_tree_4,
        R.drawable.plant_tree_5,
        R.drawable.plant_tree_6,
        R.drawable.plant_tree_7,
        R.drawable.plant_tree_8
    )
    
    private val clusterPlants = listOf(
        R.drawable.plant_cluster_1,
        R.drawable.plant_cluster_2,
        R.drawable.plant_cluster_3,
        R.drawable.plant_cluster_4,
        R.drawable.plant_cluster_5,
        R.drawable.plant_cluster_6,
        R.drawable.plant_cluster_7,
        R.drawable.plant_cluster_8
    )
    
    private val grassPlants = listOf(
        R.drawable.plant_grass_1,
        R.drawable.plant_grass_2,
        R.drawable.plant_grass_3,
        R.drawable.plant_grass_4,
        R.drawable.plant_grass_5,
        R.drawable.plant_grass_6,
        R.drawable.plant_grass_7,
        R.drawable.plant_grass_8
    )
    
    private val mushroomPlants = listOf(
        R.drawable.plant_mushroom_1,
        R.drawable.plant_mushroom_2,
        R.drawable.plant_mushroom_3,
        R.drawable.plant_mushroom_4,
        R.drawable.plant_mushroom_5,
        R.drawable.plant_mushroom_6,
        R.drawable.plant_mushroom_7,
        R.drawable.plant_mushroom_8
    )
    
    /**
     * Get the drawable resource for a specific plant type and variant.
     */
    fun getPlantDrawable(plantType: PlantType, variant: Int): Int {
        val plants = when (plantType) {
            PlantType.SIMPLE -> simplePlants
            PlantType.TREE -> treePlants
            PlantType.CLUSTER -> clusterPlants
            PlantType.GRASS -> grassPlants
            PlantType.MUSHROOM -> mushroomPlants
        }
        // Variant is 1-8, convert to 0-7 index
        val index = (variant - 1).coerceIn(0, plants.lastIndex)
        return plants[index]
    }
    
    /**
     * Get a representative drawable for a plant type (for the selector).
     */
    fun getTypeSelectorDrawable(plantType: PlantType): Int {
        return when (plantType) {
            PlantType.SIMPLE -> R.drawable.plant_simple_1
            PlantType.TREE -> R.drawable.plant_tree_3
            PlantType.CLUSTER -> R.drawable.plant_cluster_2
            PlantType.GRASS -> R.drawable.plant_grass_1
            PlantType.MUSHROOM -> R.drawable.plant_mushroom_1
        }
    }
    
    /**
     * Get all plant types in display order.
     */
    fun getAllTypes(): List<PlantType> = PlantType.entries
}
