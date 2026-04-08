package com.kartollika.mobiussharedtransitions.combo.sharedtransition

/**
 * Unique key for combo shared elements.
 *
 * @param slotId    The combo slot ID. Prevents collisions when the same product image
 *                  appears in different slots.
 * @param productId The product ID within the slot.
 * @param type      The kind of UI element being shared between screens.
 */
data class ComboSharedElementKey(
    val slotId: String,
    val productId: String,
    val type: ComboSharedElementType,
)

/**
 * All UI elements that participate in a shared-element transition between the
 * slot list and the slot detail screen.
 */
enum class ComboSharedElementType {
    /** Background container of the product card */
    Background,

    /** Product image */
    Image,

    /** Customize panel showing ingredient thumbnails */
    CustomizePanel,

    /** "Customize" / "Confirm" button */
    CustomizeButton,

    /** Product size label */
    SizeText,

    /** Product name label */
    NameText,

    /** "Sold out" badge */
    StoppedBadge,
}
