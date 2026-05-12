package com.kartollika.mobiussharedtransitions.combo.slots

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kartollika.mobiussharedtransitions.combo.ComboColors
import com.kartollika.mobiussharedtransitions.combo.ComboSlot
import com.kartollika.mobiussharedtransitions.combo.ComboState
import com.kartollika.mobiussharedtransitions.combo.ComboTypography
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.kartollika.mobiussharedtransitions.combo.blur.LocalBlurProvider
import com.kartollika.mobiussharedtransitions.combo.blur.backgroundBlurSource
import com.kartollika.mobiussharedtransitions.combo.components.BlurredCircleIconButton
import com.kartollika.mobiussharedtransitions.combo.components.ComboCenterAppBar

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ComboSlots(
    state: ComboState,
    sharedTransitionScope: SharedTransitionScope,
    modifier: Modifier = Modifier,
    onClick: (ComboSlot) -> Unit = {},
    onAddComboToCart: () -> Unit = {},
    onCloseClick: () -> Unit = {},
    slotsLazyListState: LazyListState = rememberLazyListState(),
) {
    with(sharedTransitionScope) {
        Box(modifier = modifier) {
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ComboCenterAppBar(
                    modifier = Modifier
                        .statusBarsPadding()
                        .height(64.dp),
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            with(LocalNavAnimatedContentScope.current) {
                                Text(
                                    text = state.title,
                                    modifier = Modifier
                                        .backgroundBlurSource(LocalBlurProvider.current)
                                        .animateEnterExit(),
                                    style = ComboTypography.Headline20,
                                    color = ComboColors.White,
                                    maxLines = 1,
                                )
                            }
                            if (state.description != null) {
                                Text(
                                    text = state.description,
                                    style = ComboTypography.Label12,
                                    color = ComboColors.White60,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    },
                    endContent = {
                        BlurredCircleIconButton(
                            icon = Icons.Default.Close,
                            contentDescription = "Close",
                            onClick = onCloseClick,
                        )
                    },
                )

                SlotsList(
                    state = state,
                    onClick = onClick,
                    lazyListState = slotsLazyListState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                )

                with(LocalNavAnimatedContentScope.current) {
                    PriceButton(
                        price = state.price,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .navigationBarsPadding()
                            .padding(bottom = 12.dp)
                            .animateEnterExit(
                                enter = slideInVertically { it * 4 },
                                exit = slideOutVertically { it * 4 },
                            ),
                        onClick = onAddComboToCart,
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Slot list
// ---------------------------------------------------------------------------

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.SlotsList(
    state: ComboState,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    horizontalPadding: Dp = 16.dp,
    innerPadding: Dp = 8.dp,
    onClick: (ComboSlot) -> Unit = {},
) {
    LazyRow(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(innerPadding),
        contentPadding = PaddingValues(horizontal = horizontalPadding),
        state = lazyListState,
    ) {
        itemsIndexed(state.comboSlots) { index, slot ->
            ComboSlot(
                state = slot.selectedProduct,
                modifier = Modifier
                    .testTag("combo_slot_$index")
                    .sharedBounds(
                        rememberSharedContentState(key = "item-${slot.id}"),
                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                        zIndexInOverlay = 1f,
                    )
                    .width(
                        resolveComboSlotWidth(
                            slotsCount = state.comboSlots.size,
                            outerPadding = horizontalPadding,
                            innerPadding = innerPadding,
                        )
                    ),
                onClick = { onClick(slot) },
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Price button
// ---------------------------------------------------------------------------

@Composable
private fun PriceButton(
    price: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .background(ComboColors.ButtonPrimary, RoundedCornerShape(32.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "+ $price",
            color = ComboColors.White,
            style = ComboTypography.Headline20,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}
