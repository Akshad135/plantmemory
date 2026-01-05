package com.plantmemory.app.ui.garden

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.plantmemory.app.data.JournalEntry
import com.plantmemory.app.ui.components.PlantResources
import com.plantmemory.app.ui.theme.IndigoPrimary

/**
 * Garden Canvas that displays plants in a packed, natural-looking layout.
 */
@Composable
fun GardenCanvas(
    entries: List<JournalEntry>,
    dateGroups: List<DateGroup>,
    onPlantClick: (JournalEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    
    // Determine visible date based on scroll position
    val visibleDateLabel by remember {
        derivedStateOf {
            val firstVisibleIndex = listState.firstVisibleItemIndex
            dateGroups.getOrNull(firstVisibleIndex)?.dateLabel ?: ""
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        if (entries.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = PlantResources.getTypeSelectorDrawable(
                            com.plantmemory.app.data.PlantType.SIMPLE
                        )),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.outline)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Start your garden",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap the add icon to plant your first memory",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            // Garden with plants
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(dateGroups) { dateGroup ->
                    PlantCluster(
                        entries = dateGroup.entries,
                        onPlantClick = onPlantClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
            
            // Floating date overlay
            if (visibleDateLabel.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(
                            color = IndigoPrimary.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = visibleDateLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

/**
 * A cluster of plants for a single day, arranged organically.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlantCluster(
    entries: List<JournalEntry>,
    onPlantClick: (JournalEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.Center,
        maxItemsInEachRow = 8
    ) {
        entries.forEach { entry ->
            PlantDoodle(
                entry = entry,
                size = 28.dp,
                onClick = { onPlantClick(entry) },
                modifier = Modifier
                    .offset(
                        x = ((entry.gridX - 0.5f) * 8).dp,
                        y = ((entry.gridY - 0.5f) * 8).dp
                    )
            )
        }
    }
}

/**
 * Individual plant doodle with tap interaction.
 */
@Composable
fun PlantDoodle(
    entry: JournalEntry,
    size: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.3f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "plantScale"
    )
    
    val drawableRes = PlantResources.getPlantDrawable(entry.plantType, entry.plantVariant)
    
    Image(
        painter = painterResource(id = drawableRes),
        contentDescription = "Tap to view memory",
        modifier = modifier
            .size(size)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentScale = ContentScale.Fit,
        colorFilter = ColorFilter.tint(IndigoPrimary)
    )
}
