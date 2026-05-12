package com.kartollika.mobiussharedtransitions.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Cold-start TTID / TTFD with blur on and with blur off.
 *
 * Two @Test methods rather than @Parameterized so each variant gets its own
 * named result row and Perfetto trace file — easier to diff in the report.
 */
@RunWith(AndroidJUnit4::class)
class ColdStartupBenchmark {

    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun startupBlurEnabled() = measure(blurEnabled = true)

    @Test
    fun startupBlurDisabled() = measure(blurEnabled = false)

    private fun measure(blurEnabled: Boolean) = rule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(StartupTimingMetric()),
        compilationMode = CompilationMode.None(),
        iterations = 10,
        startupMode = StartupMode.COLD,
        setupBlock = { pressHome() },
    ) {
        startActivityWithBlur(blurEnabled)
    }
}
