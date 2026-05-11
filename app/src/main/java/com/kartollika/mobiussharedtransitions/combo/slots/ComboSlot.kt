package com.kartollika.mobiussharedtransitions.combo.slots

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.ResizeMode.Companion.scaleToBounds
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.kartollika.mobiussharedtransitions.combo.ComboColors
import com.kartollika.mobiussharedtransitions.combo.ComboTypography
import com.kartollika.mobiussharedtransitions.combo.SlotProduct
import com.kartollika.mobiussharedtransitions.combo.blur.ComboBlurKey
import com.kartollika.mobiussharedtransitions.combo.blur.ComboBlurZIndex
import com.kartollika.mobiussharedtransitions.combo.blur.LocalBlurProvider
import com.kartollika.mobiussharedtransitions.combo.blur.backgroundBlurSource
import com.kartollika.mobiussharedtransitions.combo.components.CustomizeButton
import com.kartollika.mobiussharedtransitions.combo.components.IngredientThumbnail
import com.kartollika.mobiussharedtransitions.combo.components.StoppedBadge
import com.kartollika.mobiussharedtransitions.combo.sharedtransition.ComboSharedElementKey
import com.kartollika.mobiussharedtransitions.combo.sharedtransition.ComboSharedElementType

const val ComboSlotSizeRatio = 0.66f
private const val MaxImageHeightFraction = 0.44f
private const val CustomizeMaxItems = 3
private val MinContentButtonSpacing = 12.dp

@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
fun SharedTransitionScope.ComboSlot(
    state: SlotProduct,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    backgroundColor: Color = ComboColors.CardBackground,
) {
    Box(
        modifier = modifier
            .aspectRatio(ComboSlotSizeRatio)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
    ) {
        ComboSlotBackground(
            state = state,
            surface = ComboSlotSurface.Slots,
            backgroundColor = backgroundColor,
            modifier = Modifier
                .fillMaxSize()
        )

        ComboSlotContent(
            modifier = Modifier
                .zIndex(1f)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp, top = 12.dp),
            state = state,
            onClick = onClick
        )
    }
}

// ---------------------------------------------------------------------------
// Custom layout (Image | Content | Button)
// ---------------------------------------------------------------------------

private class ComboSlotMeasurePolicy : MeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureResult {
        val imageMeasurable = measurables.first { it.layoutId == ComboSlotLayoutId.Image }
        val contentMeasurable = measurables.first { it.layoutId == ComboSlotLayoutId.Content }
        val buttonMeasurable = measurables.first { it.layoutId == ComboSlotLayoutId.Button }

        val relaxed = constraints.copy(minHeight = 0, maxHeight = constraints.maxHeight)

        val buttonPlaceable = buttonMeasurable.measure(relaxed)
        val contentPlaceable = contentMeasurable.measure(relaxed)

        val maxImageHeight = (constraints.maxHeight * MaxImageHeightFraction).toInt()
        val requiredContentSpace =
            contentPlaceable.height + MinContentButtonSpacing.roundToPx() + buttonPlaceable.height
        val actualImageHeight = (maxImageHeight
            .coerceAtMost(constraints.maxHeight - requiredContentSpace))
            .coerceAtLeast(0)

        val imagePlaceable = imageMeasurable.measure(
            constraints.copy(
                minWidth = constraints.maxWidth,
                maxWidth = constraints.maxWidth,
                minHeight = actualImageHeight,
                maxHeight = actualImageHeight
            )
        )

        return layout(constraints.maxWidth, constraints.maxHeight) {
            imagePlaceable.place(x = (constraints.maxWidth - imagePlaceable.width) / 2, y = 0)
            contentPlaceable.place(
                x = (constraints.maxWidth - contentPlaceable.width) / 2,
                y = imagePlaceable.height
            )
            buttonPlaceable.place(
                x = (constraints.maxWidth - buttonPlaceable.width) / 2,
                y = constraints.maxHeight - buttonPlaceable.height
            )
        }
    }
}

@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
private fun SharedTransitionScope.ComboSlotContent(
    state: SlotProduct,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val measurePolicy = remember { ComboSlotMeasurePolicy() }

    val animatedAlpha = LocalNavAnimatedContentScope.current
        .transition.animateFloat { value ->
            if (!state.stopped) return@animateFloat 1f
            when (value) {
                EnterExitState.PreEnter -> 1f
                EnterExitState.Visible -> 0.4f
                EnterExitState.PostExit -> 0.4f
            }
        }

    Layout(
        modifier = modifier,
        measurePolicy = measurePolicy,
        content = {
            // Image
            Box(
                modifier = Modifier
                    .layoutId(ComboSlotLayoutId.Image)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(state.imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(
                                key = ComboSharedElementKey(
                                    slotId = state.slotId,
                                    productId = state.productId,
                                    type = ComboSharedElementType.Image
                                ).toString()
                            ),
                            animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                        )
                        .graphicsLayer { alpha = animatedAlpha.value }
                        .backgroundBlurSource(
                            blurState = LocalBlurProvider.current,
                            zIndex = ComboBlurZIndex.Image,
                            key = ComboBlurKey.SlotProductImage,
                        )
                )

                if (state.stopped) {
                    StoppedBadge(
                        state = state,
                        modifier = Modifier
                            .align(Alignment.Center),
                        canDrawArea = {
                            it.zIndex < ComboBlurZIndex.Image ||
                                it.key == ComboBlurKey.SlotProductImage
                        },
                    )
                }
            }

            // Info + customize panel
            Column(
                modifier = Modifier
                    .layoutId(ComboSlotLayoutId.Content)
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SlotInfo(product = state)

                if (state.customize.isNotEmpty()) {
                    SlotCustomize(
                        modifier = Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(
                                key = ComboSharedElementKey(
                                    slotId = state.slotId,
                                    productId = state.productId,
                                    type = ComboSharedElementType.CustomizePanel
                                )
                            ),
                            animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                            resizeMode = scaleToBounds(),
                        ),
                        product = state
                    )
                }
            }

            // Customize button
            CustomizeButton(
                modifier = Modifier
                    .layoutId(ComboSlotLayoutId.Button)
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(
                            key = ComboSharedElementKey(
                                slotId = state.slotId,
                                productId = state.productId,
                                type = ComboSharedElementType.CustomizeButton
                            )
                        ),
                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                        clipInOverlayDuringTransition = OverlayClip(CircleShape),
                    )
                    .fillMaxWidth()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onClick,
                    ),
            )
        }
    )
}

// ---------------------------------------------------------------------------
// Sub-components
// ---------------------------------------------------------------------------

@Composable
private fun SlotCustomize(
    product: SlotProduct,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        product.customize.take(CustomizeMaxItems).forEach { customizeItem ->
            IngredientThumbnail(
                item = customizeItem,
                contentScale = ContentScale.FillHeight,
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.SlotInfo(
    product: SlotProduct,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(
                        key = ComboSharedElementKey(
                            slotId = product.slotId,
                            productId = product.productId,
                            type = ComboSharedElementType.SizeText
                        )
                    ),
                    animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                    resizeMode = scaleToBounds(),
                )
                .graphicsLayer { alpha = if (product.stopped) 0.4f else 1f },
            text = product.size,
            style = ComboTypography.Label12,
            color = ComboColors.White60,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            modifier = Modifier
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(
                        key = ComboSharedElementKey(
                            slotId = product.slotId,
                            productId = product.productId,
                            type = ComboSharedElementType.NameText
                        )
                    ),
                    animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                    resizeMode = scaleToBounds(),
                )
                .graphicsLayer { alpha = if (product.stopped) 0.4f else 1f },
            text = product.name,
            style = ComboTypography.Label16Regular,
            maxLines = if (product.customize.isNotEmpty()) 1 else 2,
            color = ComboColors.White,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
