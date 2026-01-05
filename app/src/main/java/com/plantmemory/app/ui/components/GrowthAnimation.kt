package com.plantmemory.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.plantmemory.app.data.PlantType
import com.plantmemory.app.ui.theme.IndigoPrimary

/**
 * Animated plant growth effect when a new entry is saved.
 */
@Composable
fun GrowthAnimation(
    plantType: PlantType,
    variant: Int,
    size: Dp = 64.dp,
    onAnimationComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        // Grow animation with overshoot
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        onAnimationComplete()
    }
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(
                id = PlantResources.getPlantDrawable(plantType, variant)
            ),
            contentDescription = "Growing plant",
            modifier = Modifier
                .size(size)
                .scale(scale.value),
            colorFilter = ColorFilter.tint(IndigoPrimary)
        )
    }
}

/**
 * Particle effect for growth animation (optional enhancement).
 */
@Composable
fun GrowthParticles(
    modifier: Modifier = Modifier
) {
    // Simplified particle effect - could be enhanced with Canvas drawing
    // For now, the spring animation provides sufficient feedback
}
