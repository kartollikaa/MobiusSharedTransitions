package com.kartollika.mobiussharedtransitions.combo.slots

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val SlotWidthIfMoreThan2 = 170.dp

@Composable
fun resolveComboSlotWidth(
    slotsCount: Int = 2,
    outerPadding: Dp = 0.dp,
    innerPadding: Dp = 0.dp,
): Dp {
    val windowWidth = LocalConfiguration.current.screenWidthDp.dp
    return when {
        slotsCount > 2 -> SlotWidthIfMoreThan2
        slotsCount == 2 -> (windowWidth - outerPadding * 2 - innerPadding) / 2
        else -> windowWidth - outerPadding * 2
    }
}
