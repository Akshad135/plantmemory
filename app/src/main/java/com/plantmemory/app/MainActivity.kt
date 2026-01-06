package com.plantmemory.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.plantmemory.app.navigation.PlantMemoryNavGraph
import com.plantmemory.app.navigation.Screen
import com.plantmemory.app.ui.theme.PlantMemoryTheme

/**
 * Main activity hosting the Compose UI.
 */
class MainActivity : ComponentActivity() {
    
    companion object {
        const val EXTRA_SHOW_LATEST_MEMORY = "show_latest_memory"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val repository = (application as PlantMemoryApplication).repository
        
        // Check if opened from widget - should show latest memory popup
        val showLatestMemory = intent?.getBooleanExtra(EXTRA_SHOW_LATEST_MEMORY, false) ?: false
        
        // Consume the extra so it doesn't re-trigger on activity recreation
        if (showLatestMemory) {
            intent?.removeExtra(EXTRA_SHOW_LATEST_MEMORY)
        }
        
        setContent {
            PlantMemoryTheme {
                PlantMemoryNavGraph(
                    repository = repository,
                    showLatestMemoryOnStart = showLatestMemory,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
