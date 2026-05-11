package com.kartollika.mobiussharedtransitions.combo.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.kartollika.mobiussharedtransitions.combo.ComboColors
import com.kartollika.mobiussharedtransitions.combo.blur.ComboBlurZIndex
import com.kartollika.mobiussharedtransitions.combo.blur.LocalBlurProvider
import com.kartollika.mobiussharedtransitions.combo.blur.backgroundBlurEffect
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeArea

@OptIn(ExperimentalHazeApi::class)
@Composable
fun BlurredCircleIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    canDrawArea: ((HazeArea) -> Boolean)? = { it.zIndex < ComboBlurZIndex.SlotBackground },
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .backgroundBlurEffect(
                shape = CircleShape,
                blurState = LocalBlurProvider.current,
                blurTint = ComboColors.White10,
                fallbackBackgroundColor = ComboColors.White10,
                canDrawArea = canDrawArea,
            )
            .size(40.dp)
            .padding(10.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = ComboColors.White,
        )
    }
}
