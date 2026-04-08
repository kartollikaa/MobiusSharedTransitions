package com.kartollika.mobiussharedtransitions.combo.blur

import androidx.compose.runtime.staticCompositionLocalOf
import dev.chrisbanes.haze.HazeState

/**
 * Wrapper around [HazeState] that is passed through the composition tree via
 * [LocalBlurProvider].  The [hazeState] can be null when blur is not set up
 * (e.g. in unit tests), in which case a solid fallback colour is used instead.
 */
class BlurProvider(
    val hazeState: HazeState? = null,
)

val LocalBlurProvider = staticCompositionLocalOf { BlurProvider() }
