package com.kartollika.mobiussharedtransitions.combo.details

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kartollika.mobiussharedtransitions.combo.ComboColors
import com.kartollika.mobiussharedtransitions.combo.ComboSlot
import com.kartollika.mobiussharedtransitions.combo.ComboTypography
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.kartollika.mobiussharedtransitions.combo.blur.ComboBlurZIndex
import com.kartollika.mobiussharedtransitions.combo.blur.LocalBlurProvider
import com.kartollika.mobiussharedtransitions.combo.blur.backgroundBlurEffect
import com.kartollika.mobiussharedtransitions.combo.blur.backgroundBlurSource
import com.kartollika.mobiussharedtransitions.combo.components.BlurredCircleIconButton
import com.kartollika.mobiussharedtransitions.combo.components.ComboCenterAppBar

@Suppress("LongMethod")
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ComboDetails(
    state: ComboSlot,
    sharedTransitionScope: SharedTransitionScope,
    modifier: Modifier = Modifier,
    onProductSelect: (slotId: String, productId: String) -> Unit = { _, _ -> },
    pagerEnabled: Boolean = true,
    onBackClick: () -> Unit = {},
) {
    with(sharedTransitionScope) {
        Box(modifier = modifier) {
            // Full-screen blur tint overlay — also registers itself as a blur source so
            // that the MatchButton can request "blur excluding this tint layer".
            BackgroundTint(modifier = Modifier.fillMaxSize())

            Column(modifier = Modifier.zIndex(2f)) {
                ComboCenterAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(64.dp),
                    startContent = {
                        BlurredCircleIconButton(
                            modifier = Modifier.testTag("combo_back"),
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            onClick = onBackClick,
                        )
                    },
                )

                ComboDetailsSlotProduct(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    state = state,
                )

                ComboProductsPager(
                    modifier = Modifier.padding(bottom = 64.dp),
                    state = state,
                    onProductSelect = onProductSelect,
                    enabled = pagerEnabled,
                )

                with(LocalNavAnimatedContentScope.current) {
                    MatchButton(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 12.dp)
                            .padding(horizontal = 16.dp)
                            .renderInSharedTransitionScopeOverlay(zIndexInOverlay = 1f)
                            .animateEnterExit(
                                enter = slideInVertically { it * 4 },
                                exit = slideOutVertically { it * 4 },
                            ),
                        enabled = state.saveButtonEnabled,
                        onClick = onBackClick,
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Background tint overlay
// ---------------------------------------------------------------------------

@Composable
private fun BackgroundTint(modifier: Modifier = Modifier) {
    with(LocalNavAnimatedContentScope.current) {
        Spacer(
            modifier = modifier
                .fillMaxSize()
                .backgroundBlurSource(
                    blurState = LocalBlurProvider.current,
                    zIndex = ComboBlurZIndex.BackgroundTint,
                )
                .animateEnterExit()
                .backgroundBlurEffect(
                    blurState = LocalBlurProvider.current,
                    blurTint = ComboColors.Black20,
                    blurRadius = 8.dp,
                    canDrawArea = { it.zIndex < ComboBlurZIndex.BackgroundTint },
                )
        )
    }
}

// ---------------------------------------------------------------------------
// Centre product card
// ---------------------------------------------------------------------------

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.ComboDetailsSlotProduct(
    state: ComboSlot,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        ComboSlotProduct(
            state = state.selectedProduct,
            modifier = Modifier.sharedBounds(
                rememberSharedContentState(key = "item-${state.id}"),
                animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                zIndexInOverlay = 1f,
            ),
        )
    }
}

// ---------------------------------------------------------------------------
// Confirm / match button
// ---------------------------------------------------------------------------

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.MatchButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    val shape = RoundedCornerShape(32.dp)
    Box(
        modifier = modifier
            .height(52.dp)
            .then(
                if (enabled) {
                    Modifier.background(ComboColors.ButtonSecondaryNormal, shape)
                } else {
                    Modifier.backgroundBlurEffect(
                        blurState = LocalBlurProvider.current,
                        blurTint = ComboColors.White20,
                        shape = shape,
                        fallbackBackgroundColor = ComboColors.ButtonSecondaryNormal,
                    )
                }
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "It's a match",
            color = if (enabled) ComboColors.White else ComboColors.White20,
            style = ComboTypography.Label16Regular,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}
