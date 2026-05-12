package com.kartollika.mobiussharedtransitions

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kartollika.mobiussharedtransitions.combo.ComboScreen
import com.kartollika.mobiussharedtransitions.combo.comboPreviewState
import com.kartollika.mobiussharedtransitions.ui.theme.MobiusSharedTransitionsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val blurEnabled = intent.getBooleanExtra(EXTRA_BLUR_ENABLED, true)
        setContent {
            MobiusSharedTransitionsTheme {
                ComboScreen(
                    initialState = comboPreviewState,
                    blurEnabled = blurEnabled,
                )
            }
        }
    }

    companion object {
        const val EXTRA_BLUR_ENABLED = "blur_enabled"
    }
}
