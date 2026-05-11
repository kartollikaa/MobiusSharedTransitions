package com.kartollika.mobiussharedtransitions.combo.blur

/**
 * Z-index values for combo blur sources. Each blur effect filters its sample sources via
 * `canDrawArea` with `area.zIndex < <target layer>`, so the order here defines what each
 * layer is allowed to blur.
 */
object ComboBlurZIndex {
    const val Video = 0f
    const val BackgroundTint = 1f
    const val SlotBackground = 2f
    const val Image = 3f
}
