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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kartollika.mobiussharedtransitions.combo.ComboColors
import com.kartollika.mobiussharedtransitions.combo.ComboSlot
import com.kartollika.mobiussharedtransitions.combo.ComboTypography
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.kartollika.mobiussharedtransitions.combo.blur.ComboBlurKey
import com.kartollika.mobiussharedtransitions.combo.blur.LocalBlurProvider
import com.kartollika.mobiussharedtransitions.combo.blur.backgroundBlurEffect
import com.kartollika.mobiussharedtransitions.combo.blur.backgroundBlurSource

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
                ComboDetailsAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding(),
                    onBackClick = onBackClick,
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
                    zIndex = 1f,
                    key = ComboBlurKey.DetailsTint,
                )
                .animateEnterExit()
                .backgroundBlurEffect(
                    blurState = LocalBlurProvider.current,
                    blurTint = ComboColors.Black20,
                    blurRadius = 8.dp,
                )
        )
    }
}

// ---------------------------------------------------------------------------
// App bar
// ---------------------------------------------------------------------------

@Composable
private fun ComboDetailsAppBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .height(64.dp)
            .background(Color.Transparent),
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp)
                .size(40.dp)
                .backgroundBlurEffect(
                    shape = CircleShape,
                    blurState = LocalBlurProvider.current,
                    blurTint = ComboColors.White10,
                    fallbackBackgroundColor = ComboColors.White10,
                ),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = ComboColors.White,
            )
        }
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
            text = "Confirm selection",
            color = if (enabled) ComboColors.White else ComboColors.White20,
            style = ComboTypography.Label16Medium,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}
