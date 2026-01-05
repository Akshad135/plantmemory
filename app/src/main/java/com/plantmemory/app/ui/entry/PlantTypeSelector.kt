package com.plantmemory.app.ui.entry

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.plantmemory.app.data.PlantType
import com.plantmemory.app.ui.components.PlantResources
import com.plantmemory.app.ui.theme.IndigoPrimary
import com.plantmemory.app.ui.theme.PlantUnselected

/**
 * Plant type selector component for the entry screen.
 * Shows all 5 plant types in a centered row.
 */
@Composable
fun PlantTypeSelector(
    selectedType: PlantType,
    onTypeSelected: (PlantType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlantResources.getAllTypes().forEachIndexed { index, plantType ->
            PlantTypeButton(
                plantType = plantType,
                isSelected = plantType == selectedType,
                onClick = { onTypeSelected(plantType) }
            )
            // Add spacing between buttons (not after last one)
            if (index < PlantResources.getAllTypes().size - 1) {
                Box(modifier = Modifier.size(12.dp))
            }
        }
    }
}

@Composable
private fun PlantTypeButton(
    plantType: PlantType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        IndigoPrimary
    } else {
        Color.Transparent
    }
    
    val iconTint = if (isSelected) {
        Color.White
    } else {
        PlantUnselected
    }
    
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .then(
                if (!isSelected) {
                    Modifier.border(
                        width = 1.dp,
                        color = PlantUnselected.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(
                id = PlantResources.getTypeSelectorDrawable(plantType)
            ),
            contentDescription = plantType.name,
            modifier = Modifier.size(28.dp),
            colorFilter = ColorFilter.tint(iconTint)
        )
    }
}
