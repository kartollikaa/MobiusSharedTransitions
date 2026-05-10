package com.kartollika.mobiussharedtransitions.combo.slots

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.kartollika.mobiussharedtransitions.combo.ComboColors
import com.kartollika.mobiussharedtransitions.combo.blur.ComboBlurKey
import com.kartollika.mobiussharedtransitions.combo.blur.LocalBlurProvider
import com.kartollika.mobiussharedtransitions.combo.blur.backgroundBlurEffect

val SlotRoundingInSlots = 24.dp
val SlotRoundingInDetails = 36.dp

/**
 * Frosted-glass background for a combo slot card.
 *
 * Excludes the product-image blur areas from the blur source so that the image does not
 * appear blurred inside the card.
 */
@Composable
fun ComboSlotBackground(
    modifier: Modifier = Modifier,
    backgroundColor: Color = ComboColors.CardBackground,
    clipShape: Shape = RoundedCornerShape(SlotRoundingInSlots),
) {
    Box(
        modifier = modifier
            .backgroundBlurEffect(
                blurState = LocalBlurProvider.current,
                fallbackBackgroundColor = backgroundColor,
                shape = clipShape,
                blurRadius = 40.dp,
                canDrawArea = { area ->
                    area.key != ComboBlurKey.SlotProductImage &&
                            area.key != ComboBlurKey.DetailProductImage
                }
            )
            .border(
                width = 0.5.dp,
                color = ComboColors.White20,
                shape = clipShape,
            )
    )
}
