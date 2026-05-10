package com.kartollika.mobiussharedtransitions.combo

import android.net.Uri
import com.kartollika.mobiussharedtransitions.R

// ---------------------------------------------------------------------------
// Sample products for slot 0
// ---------------------------------------------------------------------------

val previewProduct1 = SlotProduct(
    slotId = "slot-0",
    productId = "prod-1",
    imageRes = R.drawable.ic_drink_1,
    extraPrice = "+$1.50",
    customize = sampleCustomize(),
    name = "Flat White",
    size = "350 ml",
)

val previewProduct2 = SlotProduct(
    slotId = "slot-0",
    productId = "prod-2",
    imageRes = R.drawable.ic_drink_2,
    extraPrice = "+$1.00",
    name = "Salted Caramel Latte",
    size = "450 ml",
    stopped = true,
)

val previewProduct3 = SlotProduct(
    slotId = "slot-0",
    productId = "prod-3",
    imageRes = R.drawable.ic_drink_3,
    extraPrice = "+$2.00",
    customize = sampleCustomize(),
    name = "Cappuccino",
    size = "300 ml",
)

val previewProduct4 = SlotProduct(
    slotId = "slot-0",
    productId = "prod-4",
    imageRes = R.drawable.ic_drink_4,
    extraPrice = "+$3.00",
    name = "Milkshake",
    size = "400 ml",
)

val previewProduct5 = SlotProduct(
    slotId = "slot-0",
    productId = "prod-5",
    imageRes = R.drawable.ic_drink_5,
    extraPrice = "+$3.00",
    customize = sampleCustomize(),
    name = "Matcha",
    size = "200 ml",
)

val previewProduct6 = SlotProduct(
    slotId = "slot-0",
    productId = "prod-6",
    imageRes = R.drawable.ic_drink_6,
    extraPrice = "+$2.00",
    name = "Hot Cocoa",
    size = "350 ml",
)

// ---------------------------------------------------------------------------
// Products for slot 1 — same images, different slot IDs (avoids key collision)
// ---------------------------------------------------------------------------

private fun slotProducts(slotId: String) = listOf(
    previewProduct1.copy(slotId = slotId, productId = "$slotId-prod-1"),
    previewProduct2.copy(slotId = slotId, productId = "$slotId-prod-2"),
    previewProduct3.copy(slotId = slotId, productId = "$slotId-prod-3"),
    previewProduct4.copy(slotId = slotId, productId = "$slotId-prod-4"),
    previewProduct5.copy(slotId = slotId, productId = "$slotId-prod-5"),
    previewProduct6.copy(slotId = slotId, productId = "$slotId-prod-6"),
)

// ---------------------------------------------------------------------------
// Full combo state used in the showcase
// ---------------------------------------------------------------------------

val comboPreviewState = ComboState(
    title = "Double Match",
    description = "drink + snack",
    price = "$4.50",
    videoUri = Uri.parse(
        "android.resource://com.kartollika.mobiussharedtransitions/${R.raw.combo_video}"
    ),
    comboSlots = listOf(
        ComboSlot(
            id = "slot-0",
            selectedProductIndex = 0,
            products = slotProducts("slot-0"),
        ),
        ComboSlot(
            id = "slot-1",
            selectedProductIndex = 1,
            products = slotProducts("slot-1"),
        ),
    ),
)

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

private fun sampleCustomize() = listOf(
    ProductCustomize(imageRes = R.drawable.ingredient_1),
    ProductCustomize(imageRes = R.drawable.ingredient_2),
    ProductCustomize(imageRes = R.drawable.ingredient_3),
)
