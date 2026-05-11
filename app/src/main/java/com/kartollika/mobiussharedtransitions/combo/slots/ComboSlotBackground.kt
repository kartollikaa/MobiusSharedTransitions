package com.kartollika.mobiussharedtransitions.combo.slots

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.kartollika.mobiussharedtransitions.combo.ComboColors
import com.kartollika.mobiussharedtransitions.combo.SlotProduct
import com.kartollika.mobiussharedtransitions.combo.blur.ComboBlurZIndex
import com.kartollika.mobiussharedtransitions.combo.blur.LocalBlurProvider
import com.kartollika.mobiussharedtransitions.combo.blur.backgroundBlurEffect
import com.kartollika.mobiussharedtransitions.combo.blur.backgroundBlurSource
import com.kartollika.mobiussharedtransitions.combo.sharedtransition.ComboSharedElementKey
import com.kartollika.mobiussharedtransitions.combo.sharedtransition.ComboSharedElementType
import com.kartollika.mobiussharedtransitions.combo.sharedtransition.animateNavEnterExitDp

val SlotRoundingInSlots = 24.dp
val SlotRoundingInDetails = 36.dp

enum class ComboSlotSurface(val cornerRadius: Dp) {
    Slots(SlotRoundingInSlots),
    Details(SlotRoundingInDetails),
}

/**
 * Shared-element wrapper around [ComboSlotBackground]. Used on both the slots grid and the
 * product details screen so both sides render the exact same background and animate the
 * corner radius between [SlotRoundingInSlots] and [SlotRoundingInDetails] during the
 * transition.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ComboSlotBackground(
    state: SlotProduct,
    surface: ComboSlotSurface,
    modifier: Modifier = Modifier,
    backgroundColor: Color = ComboColors.CardBackground,
) {
    val visibleRadius = surface.cornerRadius
    val hiddenRadius = when (surface) {
        ComboSlotSurface.Slots -> SlotRoundingInDetails
        ComboSlotSurface.Details -> SlotRoundingInSlots
    }

    val animatedProgress = animateNavEnterExitDp(visible = visibleRadius, hidden = hiddenRadius)

    // Defer reading animatedProgress.value to draw time so the corner-radius
    // animation invalidates only draw — not composition of border/blur/OverlayClip.
    val clipShape = remember(animatedProgress) {
        object : Shape {
            override fun createOutline(
                size: Size,
                layoutDirection: LayoutDirection,
                density: Density,
            ): Outline {
                val radius = with(density) { animatedProgress.value.toPx() }
                return Outline.Rounded(
                    RoundRect(
                        left = 0f,
                        top = 0f,
                        right = size.width,
                        bottom = size.height,
                        cornerRadius = CornerRadius(radius),
                    )
                )
            }
        }
    }

    ComboSlotBackground(
        modifier = modifier
            .backgroundBlurSource(
                blurState = LocalBlurProvider.current,
                zIndex = ComboBlurZIndex.SlotBackground,
            )
            .sharedElement(
                sharedContentState = rememberSharedContentState(
                    key = ComboSharedElementKey(
                        slotId = state.slotId,
                        productId = state.productId,
                        type = ComboSharedElementType.Background
                    )
                ),
                animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                clipInOverlayDuringTransition = OverlayClip(clipShape),
            ),
        backgroundColor = backgroundColor,
        clipShape = clipShape,
    )
}

/**
 * Frosted-glass background for a combo slot card.
 *
 * Excludes the product-image blur areas from the blur source so that the image does not
 * appear blurred inside the card.
 */
@Composable
private fun ComboSlotBackground(
    modifier: Modifier = Modifier,
    backgroundColor: Color = ComboColors.CardBackground,
    clipShape: Shape = RoundedCornerShape(SlotRoundingInSlots),
) {
    Box(
        modifier = modifier
            .backgroundBlurSource(
                blurState = LocalBlurProvider.current,
                zIndex = ComboBlurZIndex.SlotBackground,
            )
            .backgroundBlurEffect(
                blurState = LocalBlurProvider.current,
                fallbackBackgroundColor = backgroundColor,
                shape = clipShape,
                blurRadius = 40.dp,
                canDrawArea = { it.zIndex < ComboBlurZIndex.SlotBackground }
            )
            .border(
                width = 0.5.dp,
                color = ComboColors.White20,
                shape = clipShape,
            )
    )
}