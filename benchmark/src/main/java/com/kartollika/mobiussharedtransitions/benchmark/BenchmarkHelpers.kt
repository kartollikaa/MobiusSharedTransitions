package com.kartollika.mobiussharedtransitions.benchmark

import android.content.Intent
import androidx.benchmark.macro.MacrobenchmarkScope

internal const val TARGET_PACKAGE = "com.kartollika.mobiussharedtransitions"
internal const val MAIN_ACTIVITY = "com.kartollika.mobiussharedtransitions.MainActivity"
internal const val EXTRA_BLUR_ENABLED = "blur_enabled"

/**
 * Launches MainActivity with a freshly-built Intent carrying the blur toggle.
 * Used by both startup and transition benchmarks so the A/B is identical.
 */
internal fun MacrobenchmarkScope.startActivityWithBlur(blurEnabled: Boolean) {
    val intent = Intent().apply {
        setPackage(TARGET_PACKAGE)
        setClassName(TARGET_PACKAGE, MAIN_ACTIVITY)
        putExtra(EXTRA_BLUR_ENABLED, blurEnabled)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }
    startActivityAndWait(intent)
}
