package com.kartollika.mobiussharedtransitions.combo.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kartollika.mobiussharedtransitions.combo.ProductCustomize

@Composable
fun IngredientThumbnail(
    item: ProductCustomize,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    Image(
        painter = painterResource(item.imageRes),
        contentDescription = null,
        modifier = modifier
            .size(28.dp)
            .graphicsLayer { alpha = if (item.stopped) 0.4f else 1f },
        contentScale = contentScale,
    )
}
