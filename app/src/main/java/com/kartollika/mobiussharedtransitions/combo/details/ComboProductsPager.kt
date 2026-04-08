package com.kartollika.mobiussharedtransitions.combo.details

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.kartollika.mobiussharedtransitions.combo.ComboSlot
import com.kartollika.mobiussharedtransitions.combo.SlotProduct
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import androidx.compose.foundation.Image

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComboProductsPager(
    state: ComboSlot,
    modifier: Modifier = Modifier,
    onProductSelect: (slotId: String, productId: String) -> Unit = { _, _ -> },
    enabled: Boolean = true,
) {
    val pagerState = rememberPagerState(
        initialPage = state.selectedProductIndex,
        pageCount = { state.products.size }
    )
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
    val scope = rememberCoroutineScope()

    // Emit product selection whenever the settled page changes
    LaunchedEffect(pagerState, state) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->
                val product = state.products.getOrNull(page) ?: return@collect
                onProductSelect(state.id, product.productId)
            }
    }

    // Sync external state changes back to pager position
    LaunchedEffect(state.selectedProductIndex) {
        if (pagerState.currentPage != state.selectedProductIndex) {
            pagerState.animateScrollToPage(state.selectedProductIndex)
        }
    }

    Box(modifier = modifier) {
        CurrentProductHighlight(modifier = Modifier.align(Alignment.Center))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
                .align(Alignment.Center),
            contentPadding = PaddingValues(
                horizontal = screenWidthDp / 2 - 36.dp   // 36dp = half of 72dp item width
            ),
            userScrollEnabled = enabled,
        ) { page ->
            val item = state.products[page]
            val pageOffset = ((pagerState.currentPage - page) +
                pagerState.currentPageOffsetFraction).absoluteValue
            val scale = lerp(1f, 0.8f, pageOffset.coerceIn(0f, 1f))

            ProductSlotOption(
                state = item,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        alpha = if (item.stopped) 0.4f else 1f
                    }
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        enabled = enabled
                    ) {
                        scope.launch {
                            pagerState.animateScrollToPage(page)
                            onProductSelect(state.id, item.productId)
                        }
                    }
            )
        }
    }
}

@Composable
private fun ProductSlotOption(
    state: SlotProduct,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(72.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(state.imageRes),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(60.dp),
        )
    }
}
