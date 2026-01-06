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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plantmemory.app.data.JournalEntry
import com.plantmemory.app.ui.components.BottomNavBar
import com.plantmemory.app.ui.components.BottomNavItem
import com.plantmemory.app.ui.components.MemoryDetailOverlay
import com.plantmemory.app.ui.theme.IndigoPrimary

// Premium easing curve
private val SmoothEasing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)

/**
 * Main Garden Screen with Material 3 styling.
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
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stats header with M3 Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.entries.size.toString(),
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-1).sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    val daysRemaining = 365 - uiState.entries.size
                    Text(
                        text = if (daysRemaining > 0) "$daysRemaining more days of growth" else "garden complete!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Calendar grid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp)
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
            
            // Bottom section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Year chips (M3 FilterChips)
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
                            FilterChip(
                                selected = year == uiState.selectedYear,
                                onClick = { viewModel.selectYear(year) },
                                label = { 
                                    Text(
                                        text = year.toString(),
                                        fontWeight = if (year == uiState.selectedYear) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Bottom nav
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
            
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        // Memory detail overlay
        MemoryDetailOverlay(
            entry = selectedEntry,
            onDismiss = { selectedEntry = null },
            onDelete = { entry ->
                viewModel.deleteEntry(entry)
                selectedEntry = null
            }
        )
    }
}
