package com.plantmemory.app.ui.components

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.plantmemory.app.data.PlantType

private val SmoothEasing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)

enum class BottomNavItem {
    GARDEN,
    ADD
}

/**
 * Material 3 styled bottom navigation bar.
 * Clean, elevated design with smooth animations.
 */
@Composable
fun BottomNavBar(
    selectedItem: BottomNavItem,
    onItemSelected: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                clip = false
            )
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Garden icon
        NavButton(
            isSelected = selectedItem == BottomNavItem.GARDEN,
            onClick = { onItemSelected(BottomNavItem.GARDEN) },
            plantType = PlantType.CLUSTER
        )
        
        // Add icon
        NavButton(
            isSelected = selectedItem == BottomNavItem.ADD,
            onClick = { onItemSelected(BottomNavItem.ADD) },
            plantType = PlantType.GRASS
        )
    }
}

@Composable
private fun NavButton(
    isSelected: Boolean,
    onClick: () -> Unit,
    plantType: PlantType,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.92f,
        animationSpec = tween(200, easing = SmoothEasing),
        label = "scale"
    )
    
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        Color.Transparent
    }
    
    val iconTint = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    }
    
    Box(
        modifier = modifier
            .size(52.dp)
            .scale(scale)
            .clip(RoundedCornerShape(14.dp))
            .background(containerColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(
                id = PlantResources.getTypeSelectorDrawable(plantType)
            ),
            contentDescription = if (isSelected) "Selected" else "Not selected",
            modifier = Modifier.size(26.dp),
            colorFilter = ColorFilter.tint(iconTint)
        )
    }
}
