package com.kartollika.mobiussharedtransitions.combo.blur

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kartollika.mobiussharedtransitions.combo.ComboColors
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeArea
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource

fun Modifier.backgroundBlurSource(
    blurState: BlurProvider?,
    zIndex: Float = 0f,
    key: Any? = null,
): Modifier {
    val blurSourceModifier = if (blurState?.hazeState != null) {
        Modifier.hazeSource(blurState.hazeState, zIndex, key)
    } else {
        Modifier
    }

    return this then blurSourceModifier
}

/**
 * Modifier extension that applies a blur effect with a fallback background.
 *
 * @param shape The shape to clip and apply the background to.
 * @param blurState The HazeState to use for the blur effect. If null, fallback background is used.
 * @param blurTint The tint color for the blur effect.
 * @param fallbackBackgroundColor The background color to use when blur is not available.
 * @param blurRadius The radius of the blur effect.
 */
@OptIn(ExperimentalHazeApi::class)
@Suppress("MagicNumber")
@Composable
fun Modifier.backgroundBlurEffect(
    blurState: BlurProvider?,
    shape: Shape = RectangleShape,
    blurTint: Color = ComboColors.Black10,
    fallbackBackgroundColor: Color = ComboColors.BackgroundPrimary,
    blurRadius: Dp = 16.dp,
    canDrawArea: ((HazeArea) -> Boolean)? = null,
): Modifier {
    val blurModifier = if (blurState == null || blurState.hazeState == null) {
        Modifier
            .background(
                color = fallbackBackgroundColor,
                shape = shape
            )
    } else {
        Modifier
            .clip(shape)
            .hazeEffect(
                blurState.hazeState,
                blurMaterial(
                    containerColor = blurTint,
                    blurRadius = blurRadius,
                )
            ) {
                noiseFactor = 0f
                inputScale = HazeInputScale.Fixed(0.5f)
                fallbackTint = HazeTint(fallbackBackgroundColor)
                this.canDrawArea = canDrawArea
            }
    }

    return this then blurModifier
}

@Composable
private fun blurMaterial(
    containerColor: Color,
    blurRadius: Dp = 40.dp,
): HazeStyle = remember(
    containerColor,
    blurRadius
) {
    HazeStyle(
        blurRadius = blurRadius,
        tint = HazeTint(containerColor),
    )
}
