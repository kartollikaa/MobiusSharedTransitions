package com.kartollika.mobiussharedtransitions.combo

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import com.kartollika.mobiussharedtransitions.combo.blur.BlurProvider
import com.kartollika.mobiussharedtransitions.combo.blur.LocalBlurProvider
import com.kartollika.mobiussharedtransitions.combo.blur.backgroundBlurSource
import com.kartollika.mobiussharedtransitions.combo.details.ComboDetails
import com.kartollika.mobiussharedtransitions.combo.slots.ComboSlots
import com.kartollika.mobiussharedtransitions.combo.video.ComboVideo
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.serialization.Serializable

private const val TransitionDurationMs = 700
private const val PredictiveBackEnterMs = 200
private const val PredictiveBackExitMs = 100

// Navigation keys for navigation3 NavDisplay.
@Serializable
data object ComboSlotsKey : NavKey

@Serializable
data class ComboSlotDetailsKey(val slotId: String) : NavKey

private fun comboTransitionSpec():
    AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {
    ContentTransform(
        fadeIn(animationSpec = tween(TransitionDurationMs)),
        fadeOut(animationSpec = tween(TransitionDurationMs)),
    )
}

private fun comboPredictiveBackTransitionSpec():
    AnimatedContentTransitionScope<Scene<NavKey>>.(Int) -> ContentTransform = {
    ContentTransform(
        fadeIn(animationSpec = tween(PredictiveBackEnterMs)),
        fadeOut(animationSpec = tween(PredictiveBackExitMs)),
    )
}

/**
 * Root composable for the Combo showcase.
 *
 * Sets up:
 * - Haze [HazeState] + [BlurProvider] for the frosted-glass effects
 * - Gradient background that acts as the Haze blur source
 * - [SharedTransitionLayout] wrapping [NavDisplay]-based navigation
 *
 * @param initialState  The initial [ComboState] populated from [comboPreviewState].
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ComboScreen(
    initialState: ComboState = comboPreviewState,
) {
    // Mutable state so that product-selection interactions update the UI.
    var state by remember { mutableStateOf(initialState) }
    val backStack = rememberNavBackStack(ComboSlotsKey)

    // Haze blur infrastructure
    val hazeState = rememberHazeState(blurEnabled = true)
    val blurProvider = remember(hazeState) { BlurProvider(hazeState) }

    CompositionLocalProvider(LocalBlurProvider provides blurProvider) {
        Box(Modifier.fillMaxSize()) {
            // ----------------------------------------------------------------
            // Background — video or gradient fallback, acts as blur source.
            // PlayerView uses TextureView (via XML) so Haze can capture it.
            // ----------------------------------------------------------------
            val videoUri = state.videoUri
            if (videoUri != null) {
                ComboVideo(
                    videoUri = videoUri,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    ComboColors.BackgroundTop,
                                    ComboColors.BackgroundBottom,
                                )
                            )
                        )
                        .backgroundBlurSource(blurProvider)
                )
            }

            // ----------------------------------------------------------------
            // Navigation + shared-element transitions
            // ----------------------------------------------------------------
            SharedTransitionLayout(Modifier.fillMaxSize()) {
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    transitionSpec = comboTransitionSpec(),
                    popTransitionSpec = comboTransitionSpec(),
                    predictivePopTransitionSpec = comboPredictiveBackTransitionSpec(),
                    entryProvider = entryProvider {
                        entry<ComboSlotsKey> {
                            ComboSlots(
                                sharedTransitionScope = this@SharedTransitionLayout,
                                modifier = Modifier.fillMaxSize(),
                                state = state,
                                onClick = { slot ->
                                    backStack.add(ComboSlotDetailsKey(slot.id))
                                },
                                onAddComboToCart = { /* demo — no real cart */ },
                                onCloseClick = { /* demo */ },
                            )
                        }

                        entry<ComboSlotDetailsKey> { key ->
                            ComboDetails(
                                sharedTransitionScope = this@SharedTransitionLayout,
                                modifier = Modifier.fillMaxSize(),
                                state = state.comboSlots.first { it.id == key.slotId },
                                pagerEnabled = backStack.lastOrNull() is ComboSlotDetailsKey,
                                onBackClick = { backStack.removeLastOrNull() },
                                onProductSelect = { slotId, productId ->
                                    state = state.updateSelectedProduct(slotId, productId)
                                },
                            )
                        }
                    }
                )
            }
        }
    }
}
