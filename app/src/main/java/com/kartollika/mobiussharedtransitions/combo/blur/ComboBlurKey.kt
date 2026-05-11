package com.kartollika.mobiussharedtransitions.combo.blur

/**
 * Keys for the two product-image blur sources. Used by [StoppedBadge] to keep blurring its
 * own image while ignoring the other screen's image during a shared-element transition —
 * z-index alone can't distinguish two sources at the same layer.
 */
sealed class ComboBlurKey {

    /** Product image blur source in the slots list view. */
    data object SlotProductImage : ComboBlurKey()

    /** Product image blur source in the detail view. */
    data object DetailProductImage : ComboBlurKey()
}
