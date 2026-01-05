package com.plantmemory.app.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.plantmemory.app.data.PlantType
import com.plantmemory.app.ui.theme.IndigoPrimary

// Premium easing curve
private val SmoothEasing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)

enum class BottomNavItem {
    GARDEN,
    ADD
}

/**
 * Bottom navigation bar with garden view and add memory icons.
 * Smooth animations matching iOS/Samsung style.
 */
@Composable
fun BottomNavBar(
    selectedItem: BottomNavItem,
    onItemSelected: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = Color.Transparent
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Garden icon (left)
        NavButton(
            isSelected = selectedItem == BottomNavItem.GARDEN,
            onClick = { onItemSelected(BottomNavItem.GARDEN) },
            plantType = PlantType.CLUSTER  // Using cluster plant for garden icon
        )
        
        // Add icon (right)
        NavButton(
            isSelected = selectedItem == BottomNavItem.ADD,
            onClick = { onItemSelected(BottomNavItem.ADD) },
            plantType = PlantType.GRASS  // Using grass/tree for add icon
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
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) IndigoPrimary else Color.Transparent,
        animationSpec = tween(durationMillis = 200, easing = SmoothEasing),
        label = "bgColor"
    )
    
    val iconTint by animateColorAsState(
        targetValue = if (isSelected) Color.White else IndigoPrimary.copy(alpha = 0.5f),
        animationSpec = tween(durationMillis = 200, easing = SmoothEasing),
        label = "iconTint"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.92f,
        animationSpec = tween(durationMillis = 200, easing = SmoothEasing),
        label = "scale"
    )
    
    Box(
        modifier = modifier
            .size(56.dp)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(
                id = PlantResources.getTypeSelectorDrawable(plantType)
            ),
            contentDescription = if (isSelected) "Selected" else "Not selected",
            modifier = Modifier.size(28.dp),
            colorFilter = ColorFilter.tint(iconTint)
        )
    }
}
