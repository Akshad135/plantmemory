package com.plantmemory.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.plantmemory.app.data.JournalEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val SmoothEasing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)

// Delete button red color
private val DeleteRed = Color(0xFFEF4444)
private val DeleteRedLight = Color(0xFFFEE2E2)

/**
 * Material 3 styled dialog showing memory details with delete option.
 */
@Composable
fun MemoryDetailDialog(
    entry: JournalEntry,
    onDismiss: () -> Unit,
    onDelete: (JournalEntry) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("EEEE, MMMM d", Locale.US) }
    val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.US) }
    val date = Date(entry.timestamp)
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    ),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 16.dp,
                tonalElevation = 6.dp
            ) {
                Box {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Date header
                        Text(
                            text = dateFormat.format(date),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Text(
                            text = timeFormat.format(date).lowercase(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Plant icon with container
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    RoundedCornerShape(24.dp)
                                )
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(
                                    id = PlantResources.getPlantDrawable(entry.plantType, entry.plantVariant)
                                ),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Memory text
                        Text(
                            text = entry.text,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    // Delete button - top right corner
                    IconButton(
                        onClick = { onDelete(entry) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .size(40.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = DeleteRedLight,
                            contentColor = DeleteRed
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete memory",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Overlay for showing memory detail with smooth animation.
 */
@Composable
fun MemoryDetailOverlay(
    entry: JournalEntry?,
    onDismiss: () -> Unit,
    onDelete: (JournalEntry) -> Unit = {}
) {
    AnimatedVisibility(
        visible = entry != null,
        enter = fadeIn(tween(250, easing = SmoothEasing)) + 
                scaleIn(initialScale = 0.92f, animationSpec = tween(250, easing = SmoothEasing)),
        exit = fadeOut(tween(200, easing = SmoothEasing)) + 
               scaleOut(targetScale = 0.92f, animationSpec = tween(200, easing = SmoothEasing))
    ) {
        entry?.let {
            MemoryDetailDialog(entry = it, onDismiss = onDismiss, onDelete = onDelete)
        }
    }
}
