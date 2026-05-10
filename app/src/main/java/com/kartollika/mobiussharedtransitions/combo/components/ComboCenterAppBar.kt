package com.kartollika.mobiussharedtransitions.combo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ComboCenterAppBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    startContent: @Composable () -> Unit = {},
    title: @Composable () -> Unit = {},
    endContent: @Composable () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor),
    ) {
        Box(Modifier.align(Alignment.CenterStart).padding(start = 16.dp)) { startContent() }
        Box(Modifier.align(Alignment.Center)) { title() }
        Box(Modifier.align(Alignment.CenterEnd).padding(end = 16.dp)) { endContent() }
    }
}
