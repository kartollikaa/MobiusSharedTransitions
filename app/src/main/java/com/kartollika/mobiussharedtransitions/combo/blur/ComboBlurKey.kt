package com.kartollika.mobiussharedtransitions.combo.blur

/**
 * Type-safe keys for combo blur sources.
 *
 * Used to prevent drawing one blur on top of another, which would degrade the visual quality
 * (e.g. blurring an already-blurred image looks wrong).
 */
sealed class ComboBlurKey {

    /** Product image blur source in the slots list view. */
    data object SlotProductImage : ComboBlurKey()

    /** Product image blur source in the detail view. */
    data object DetailProductImage : ComboBlurKey()

    /** Full-screen tint overlay blur source shown behind the detail card. */
    data object DetailsTint : ComboBlurKey()
}
