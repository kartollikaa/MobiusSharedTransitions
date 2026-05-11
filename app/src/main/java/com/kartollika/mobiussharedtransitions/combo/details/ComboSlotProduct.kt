package com.kartollika.mobiussharedtransitions.combo.details

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.ResizeMode.Companion.scaleToBounds
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.kartollika.mobiussharedtransitions.combo.slots.ComboSlotBackground
import com.kartollika.mobiussharedtransitions.combo.slots.ComboSlotSurface

private const val ChangeProductAnimationDurationMs = 100
private val ProductImageHeight = 308.dp
private const val ProductImageAspectRatio = 204f / 308f

@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
fun SharedTransitionScope.ComboSlotProduct(
    state: SlotProduct,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    backgroundColor: Color = ComboColors.CardBackground,
) {
    Box(
        modifier = modifier
            .height(ProductImageHeight)
            .width(204.dp)
            .aspectRatio(ProductImageAspectRatio),
    ) {
        ComboSlotBackground(
            modifier = Modifier.fillMaxSize().zIndex(1f),
            state = state,
            surface = ComboSlotSurface.Details,
            backgroundColor = backgroundColor,
        )
        SlotProductContent(
            state = state,
            onClick = onClick,
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f)
                .padding(horizontal = 16.dp)
                .padding(bottom = 20.dp, top = 12.dp),
        )
    }
}

// ---------------------------------------------------------------------------
// Content
// ---------------------------------------------------------------------------

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.SlotProductContent(
    state: SlotProduct,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SlotProductImage(
            state = state,
            modifier = Modifier
                .size(104.dp)
                .requiredSize(140.dp)
                .offset(y = (-24).dp)
        )
        SlotProductInfo(
            state = state,
            modifier = Modifier.fillMaxHeight(),
            onClick = onClick,
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.SlotProductImage(
    state: SlotProduct,
    modifier: Modifier = Modifier,
) {
    val animatedAlpha = LocalNavAnimatedContentScope.current
        .transition.animateFloat { value ->
            if (!state.stopped) return@animateFloat 1f
            when (value) {
                EnterExitState.PreEnter -> 0.4f
                EnterExitState.Visible -> 1f
                EnterExitState.PostExit -> 1f
            }
        }

    Box(modifier = modifier) {
        // AnimatedContent allows swapping the image smoothly when the user picks a
        // different product from the pager.
        AnimatedContent(
            modifier = Modifier
                .matchParentSize()
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
                ),
            targetState = state.imageRes,
            transitionSpec = {
                scaleIn(
                    initialScale = 0.85f,
                    animationSpec = tween(durationMillis = ChangeProductAnimationDurationMs)
                )
                    .togetherWith(ExitTransition.None)
                    .using(SizeTransform(clip = false))
            }
        ) { targetImageRes ->
            Image(
                painter = painterResource(targetImageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .backgroundBlurSource(
                        blurState = LocalBlurProvider.current,
                        zIndex = ComboBlurZIndex.Image,
                        key = ComboBlurKey.DetailProductImage,
                    )
                    .graphicsLayer { alpha = animatedAlpha.value },
                contentScale = ContentScale.Fit,
            )
        }

        if (state.stopped) {
            StoppedBadge(
                state = state,
                modifier = Modifier
                    .align(Alignment.Center),
                canDrawArea = {
                    it.zIndex < ComboBlurZIndex.Image ||
                        it.key == ComboBlurKey.DetailProductImage
                },
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.SlotProductInfo(
    state: SlotProduct,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ProductInfoLabels(product = state)
            SlotProductCustomize(
                modifier = Modifier.padding(top = 10.dp),
                product = state,
                onClick = onClick,
            )
        }
        SlotProductExtraPrice(product = state)
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.ProductInfoLabels(
    product: SlotProduct,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(
                    key = ComboSharedElementKey(
                        slotId = product.slotId,
                        productId = product.productId,
                        type = ComboSharedElementType.SizeText
                    )
                ),
                animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                resizeMode = scaleToBounds(),
            ),
            text = product.size,
            style = ComboTypography.Label14,
            color = ComboColors.White60,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            modifier = Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(
                    key = ComboSharedElementKey(
                        slotId = product.slotId,
                        productId = product.productId,
                        type = ComboSharedElementType.NameText
                    )
                ),
                animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                resizeMode = scaleToBounds(),
            ),
            text = product.name,
            style = ComboTypography.Headline20,
            maxLines = if (product.customize.isNotEmpty()) 1 else 2,
            color = ComboColors.White,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SlotProductExtraPrice(
    product: SlotProduct,
    modifier: Modifier = Modifier,
) {
    if (product.stopped) {
        Text(
            modifier = modifier,
            text = "Sold out",
            color = ComboColors.White60,
            style = ComboTypography.Label16Regular,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    } else {
        Text(
            modifier = modifier,
            text = product.extraPrice,
            color = ComboColors.White,
            style = ComboTypography.Label16Regular,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.SlotProductCustomize(
    product: SlotProduct,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (product.customize.isEmpty()) {
            CustomizeButton(
                modifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(
                        key = ComboSharedElementKey(
                            slotId = product.slotId,
                            productId = product.productId,
                            type = ComboSharedElementType.CustomizeButton
                        )
                    ),
                    animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                    clipInOverlayDuringTransition = OverlayClip(CircleShape),
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                ),
            )
        } else {
            CustomizeIngredients(product = product)
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.CustomizeIngredients(
    product: SlotProduct,
) {
    Row(
        modifier = Modifier
            .background(ComboColors.White10, CircleShape)
            .height(40.dp)
            .padding(horizontal = 12.dp)
            .sharedBounds(
                sharedContentState = rememberSharedContentState(
                    key = ComboSharedElementKey(
                        slotId = product.slotId,
                        productId = product.productId,
                        type = ComboSharedElementType.CustomizePanel
                    )
                ),
                animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                resizeMode = scaleToBounds(),
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        product.customize.take(3).forEach { item ->
            IngredientThumbnail(item = item)
        }
    }
}
