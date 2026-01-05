package com.plantmemory.app.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.plantmemory.app.data.JournalRepository
import com.plantmemory.app.ui.entry.EntryScreen
import com.plantmemory.app.ui.entry.EntryViewModel
import com.plantmemory.app.ui.garden.GardenScreen
import com.plantmemory.app.ui.garden.GardenViewModel

/**
 * Navigation destinations for the app.
 */
sealed class Screen(val route: String) {
    data object Garden : Screen("garden")
    data object Entry : Screen("entry")
}

// Premium easing curves (iOS/Samsung style)
private val SmoothEasing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
private val DecelerateEasing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
private val AccelerateEasing = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)

// Animation durations
private const val ENTER_DURATION = 350
private const val EXIT_DURATION = 250

/**
 * Main navigation graph with smooth, premium animations.
 */
@Composable
fun PlantMemoryNavGraph(
    repository: JournalRepository,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Garden.route,
    showLatestMemoryOnStart: Boolean = false
) {
    val context = LocalContext.current
    
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Garden (Main) Screen
        composable(
            route = Screen.Garden.route,
            enterTransition = { gardenEnterTransition() },
            exitTransition = { gardenExitTransition() },
            popEnterTransition = { gardenPopEnterTransition() },
            popExitTransition = { gardenPopExitTransition() }
        ) {
            val gardenViewModel: GardenViewModel = viewModel(
                factory = GardenViewModel.Factory(repository)
            )
            
            GardenScreen(
                viewModel = gardenViewModel,
                onNavigateToEntry = {
                    navController.navigate(Screen.Entry.route)
                },
                showLatestMemoryOnStart = showLatestMemoryOnStart
            )
        }
        
        // Entry Screen
        composable(
            route = Screen.Entry.route,
            enterTransition = { entryEnterTransition() },
            exitTransition = { entryExitTransition() },
            popEnterTransition = { entryPopEnterTransition() },
            popExitTransition = { entryPopExitTransition() }
        ) {
            val entryViewModel: EntryViewModel = viewModel(
                factory = EntryViewModel.Factory(repository, context)
            )
            
            EntryScreen(
                viewModel = entryViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

// Garden screen transitions - smooth fade and subtle scale
private fun gardenEnterTransition(): EnterTransition {
    return fadeIn(
        animationSpec = tween(
            durationMillis = ENTER_DURATION,
            easing = DecelerateEasing
        )
    ) + scaleIn(
        initialScale = 0.96f,
        animationSpec = tween(
            durationMillis = ENTER_DURATION,
            easing = DecelerateEasing
        )
    )
}

private fun gardenExitTransition(): ExitTransition {
    return fadeOut(
        animationSpec = tween(
            durationMillis = EXIT_DURATION,
            easing = AccelerateEasing
        )
    ) + scaleOut(
        targetScale = 0.94f,
        animationSpec = tween(
            durationMillis = EXIT_DURATION,
            easing = AccelerateEasing
        )
    )
}

private fun gardenPopEnterTransition(): EnterTransition {
    return fadeIn(
        animationSpec = tween(
            durationMillis = ENTER_DURATION,
            easing = DecelerateEasing
        )
    ) + scaleIn(
        initialScale = 0.94f,
        animationSpec = tween(
            durationMillis = ENTER_DURATION,
            easing = DecelerateEasing
        )
    )
}

private fun gardenPopExitTransition(): ExitTransition {
    return fadeOut(
        animationSpec = tween(
            durationMillis = EXIT_DURATION,
            easing = AccelerateEasing
        )
    )
}

// Entry screen transitions - smooth slide up from bottom
private fun entryEnterTransition(): EnterTransition {
    return fadeIn(
        animationSpec = tween(
            durationMillis = ENTER_DURATION,
            easing = DecelerateEasing
        )
    ) + slideInVertically(
        animationSpec = tween(
            durationMillis = ENTER_DURATION,
            easing = DecelerateEasing
        ),
        initialOffsetY = { it / 4 }
    )
}

private fun entryExitTransition(): ExitTransition {
    return fadeOut(
        animationSpec = tween(
            durationMillis = EXIT_DURATION,
            easing = AccelerateEasing
        )
    )
}

private fun entryPopEnterTransition(): EnterTransition {
    return fadeIn(
        animationSpec = tween(
            durationMillis = ENTER_DURATION,
            easing = DecelerateEasing
        )
    )
}

private fun entryPopExitTransition(): ExitTransition {
    return fadeOut(
        animationSpec = tween(
            durationMillis = ENTER_DURATION,
            easing = AccelerateEasing
        )
    ) + slideOutVertically(
        animationSpec = tween(
            durationMillis = ENTER_DURATION,
            easing = AccelerateEasing
        ),
        targetOffsetY = { it / 4 }
    )
}
