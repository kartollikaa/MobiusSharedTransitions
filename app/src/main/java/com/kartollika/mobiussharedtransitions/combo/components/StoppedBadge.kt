package com.kartollika.mobiussharedtransitions.combo.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.kartollika.mobiussharedtransitions.R
import com.kartollika.mobiussharedtransitions.combo.ComboColors
import com.kartollika.mobiussharedtransitions.combo.ComboTypography
import com.kartollika.mobiussharedtransitions.combo.SlotProduct
import com.kartollika.mobiussharedtransitions.combo.blur.LocalBlurProvider
import com.kartollika.mobiussharedtransitions.combo.blur.backgroundBlurEffect
import com.kartollika.mobiussharedtransitions.combo.sharedtransition.ComboSharedElementKey
import com.kartollika.mobiussharedtransitions.combo.sharedtransition.ComboSharedElementType
import dev.chrisbanes.haze.HazeArea

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.StoppedBadge(
    state: SlotProduct,
    modifier: Modifier = Modifier,
    canDrawArea: ((HazeArea) -> Boolean)? = null,
) {
    Text(
        modifier = modifier
            .sharedElement(
                sharedContentState = rememberSharedContentState(
                    key = ComboSharedElementKey(
                        slotId = state.slotId,
                        productId = state.productId,
                        type = ComboSharedElementType.StoppedBadge
                    )
                ),
                animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                zIndexInOverlay = 1f,
            )
            .backgroundBlurEffect(
                blurState = LocalBlurProvider.current,
                shape = RoundedCornerShape(10.dp),
                blurTint = ComboColors.White10,
                fallbackBackgroundColor = ComboColors.Black60,
                blurRadius = 32.dp,
                canDrawArea = canDrawArea,
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        text = stringResource(R.string.combo_slot_sold_out_badge),
        style = ComboTypography.Label14,
        color = ComboColors.White,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}
