package com.kartollika.mobiussharedtransitions.combo.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.dp
import com.kartollika.mobiussharedtransitions.combo.ComboColors

/**
 * Pill-shaped highlight that sits behind the centered product thumbnail in the
 * product-selection pager at the bottom of the detail screen.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CurrentProductHighlight(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(72.dp)
            .height(112.dp)
            .innerShadow(
                shape = CircleShape,
                shadow = Shadow(
                    radius = 12.dp,
                    color = ComboColors.White.copy(alpha = 0.23f)
                )
            )
            .border(
                width = 0.5.dp,
                color = ComboColors.White20,
                shape = CircleShape,
            )
    ) {
        Spacer(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp)
                .size(4.dp)
                .background(ComboColors.White30, CircleShape)
        )
    }
}
