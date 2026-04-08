package com.kartollika.mobiussharedtransitions.combo

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable

@Immutable
data class ComboState(
    val title: String = "",
    val description: String? = null,
    val price: String = "",
    val videoUri: Uri? = null,
    val comboSlots: List<ComboSlot> = emptyList(),
) {
    fun updateSelectedProduct(slotId: String, productId: String): ComboState {
        return copy(
            comboSlots = comboSlots.map { slot ->
                if (slot.id == slotId) {
                    val newIndex = slot.products.indexOfFirst { it.productId == productId }
                    if (newIndex >= 0) slot.copy(selectedProductIndex = newIndex) else slot
                } else {
                    slot
                }
            }
        )
    }
}

@Immutable
data class ComboSlot(
    val id: String,
    val selectedProductIndex: Int,
    val products: List<SlotProduct>,
    val saveButtonEnabled: Boolean = true,
) {
    val selectedProduct: SlotProduct get() = products[selectedProductIndex]
}

@Immutable
data class SlotProduct(
    val slotId: String,
    val productId: String,
    @DrawableRes val imageRes: Int,
    val extraPrice: String,
    val name: String,
    val size: String,
    val customize: List<ProductCustomize> = emptyList(),
    val stopped: Boolean = false,
)

@Immutable
data class ProductCustomize(
    @DrawableRes val imageRes: Int,
    val stopped: Boolean = false,
)
