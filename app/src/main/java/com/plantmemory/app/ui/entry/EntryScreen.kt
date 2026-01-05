package com.plantmemory.app.ui.entry

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.plantmemory.app.ui.components.BottomNavBar
import com.plantmemory.app.ui.components.BottomNavItem
import com.plantmemory.app.ui.theme.IndigoPrimary
import com.plantmemory.app.ui.theme.TextMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

/**
 * Entry screen for adding new journal entries.
 * Includes bottom navigation for easy switching between screens.
 */
@Composable
fun EntryScreen(
    viewModel: EntryViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState
    val focusRequester = remember { FocusRequester() }
    val dateFormat = remember { SimpleDateFormat("EEEE, MM.dd", Locale.US) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
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
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (abs(dragOffset) > 100) {
                                if (dragOffset > 0) {
                                    viewModel.goToPreviousDay()
                                } else if (viewModel.canGoToNextDay()) {
                                    viewModel.goToNextDay()
                                }
                            }
                            dragOffset = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            dragOffset += dragAmount
                        }
                    )
                }
                .padding(horizontal = 24.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // "edit" label chip
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "edit",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Date selector with arrows
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { viewModel.goToPreviousDay() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous day",
                        tint = IndigoPrimary
                    )
                }
                
                Text(
                    text = dateFormat.format(Date(uiState.selectedDate)).lowercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = IndigoPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                
                IconButton(
                    onClick = { viewModel.goToNextDay() },
                    enabled = viewModel.canGoToNextDay()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next day",
                        tint = if (viewModel.canGoToNextDay()) IndigoPrimary else IndigoPrimary.copy(alpha = 0.3f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Plant type selector
            PlantTypeSelector(
                selectedType = uiState.selectedPlantType,
                onTypeSelected = { viewModel.selectPlantType(it) },
                modifier = Modifier.fillMaxWidth(),
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Text input area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                BasicTextField(
                    value = uiState.text,
                    onValueChange = { viewModel.updateText(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    textStyle = TextStyle(
                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    cursorBrush = SolidColor(IndigoPrimary),
                    decorationBox = { innerTextField ->
                        Box {
                            if (uiState.text.isEmpty()) {
                                Text(
                                    text = "Write your memory...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextMuted
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
            
            // Done button
            val smoothEasing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
            AnimatedVisibility(
                visible = uiState.text.isNotBlank(),
                enter = fadeIn(tween(200, easing = smoothEasing)) + 
                        scaleIn(initialScale = 0.9f, animationSpec = tween(200, easing = smoothEasing)),
                exit = fadeOut(tween(150, easing = smoothEasing)) + 
                       scaleOut(targetScale = 0.9f, animationSpec = tween(150, easing = smoothEasing))
            ) {
                Button(
                    onClick = {
                        viewModel.saveEntry {
                            viewModel.reset()
                            onNavigateBack()
                        }
                    },
                    enabled = !uiState.isSaving,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IndigoPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = if (uiState.isSaving) "Saving..." else "Done",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
            
            // Bottom navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavBar(
                    selectedItem = BottomNavItem.ADD,
                    onItemSelected = { item ->
                        if (item == BottomNavItem.GARDEN) {
                            onNavigateBack()
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
