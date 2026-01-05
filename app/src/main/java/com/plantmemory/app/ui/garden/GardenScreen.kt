package com.plantmemory.app.ui.garden

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.plantmemory.app.data.JournalEntry
import com.plantmemory.app.ui.components.BottomNavBar
import com.plantmemory.app.ui.components.BottomNavItem
import com.plantmemory.app.ui.components.MemoryDetailOverlay
import com.plantmemory.app.ui.theme.IndigoPrimary

/**
 * Main Garden Screen showing the calendar grid of plants.
 */
@Composable
fun GardenScreen(
    viewModel: GardenViewModel,
    onNavigateToEntry: () -> Unit,
    showLatestMemoryOnStart: Boolean = false,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedEntry by remember { mutableStateOf<JournalEntry?>(null) }
    var selectedNavItem by remember { mutableStateOf(BottomNavItem.GARDEN) }
    var hasShownLatestMemory by remember { mutableStateOf(false) }
    
    // Auto-show latest memory if opened from widget
    LaunchedEffect(showLatestMemoryOnStart, uiState.entries) {
        if (showLatestMemoryOnStart && !hasShownLatestMemory && uiState.entries.isNotEmpty()) {
            // Get the latest entry by timestamp
            val latest = uiState.entries.maxByOrNull { it.timestamp }
            if (latest != null) {
                selectedEntry = latest
                hasShownLatestMemory = true
            }
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Stats header (entry count and days remaining)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = uiState.entries.size.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                val daysRemaining = 365 - uiState.entries.size
                Text(
                    text = if (daysRemaining > 0) "$daysRemaining more days of growth" else "garden complete!",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Calendar grid - takes available space
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                CalendarGrid(
                    entries = uiState.entries,
                    year = uiState.selectedYear,
                    onDayClick = { day ->
                        day.entry?.let { selectedEntry = it }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Bottom section: Two rows - Year tabs on top, Nav in center
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Year tabs row (horizontally scrollable)
                if (uiState.availableYears.size > 1) {
                    val scrollState = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .horizontalScroll(scrollState)
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        uiState.availableYears.forEach { year ->
                            YearTab(
                                year = year,
                                isSelected = year == uiState.selectedYear,
                                onClick = { viewModel.selectYear(year) }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Bottom nav centered
                BottomNavBar(
                    selectedItem = selectedNavItem,
                    onItemSelected = { item ->
                        selectedNavItem = item
                        if (item == BottomNavItem.ADD) {
                            onNavigateToEntry()
                            selectedNavItem = BottomNavItem.GARDEN
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Memory detail overlay
        MemoryDetailOverlay(
            entry = selectedEntry,
            onDismiss = { selectedEntry = null }
        )
    }
}

/**
 * Individual year tab chip.
 */
@Composable
fun YearTab(
    year: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val smoothEasing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.95f,
        animationSpec = tween(durationMillis = 200, easing = smoothEasing),
        label = "tabScale"
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) IndigoPrimary else IndigoPrimary.copy(alpha = 0.1f)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = year.toString(),
            style = MaterialTheme.typography.labelLarge,
            color = if (isSelected) Color.White else IndigoPrimary,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}
