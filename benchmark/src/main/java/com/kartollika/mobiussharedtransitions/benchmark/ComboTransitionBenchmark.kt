package com.kartollika.mobiussharedtransitions.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MemoryUsageMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Frame-timing for the list ⇆ detail navigation, with blur on and off.
 *
 * `frameDurationCpuMs` reports P50/P95/P99 across both the forward (700 ms
 * fade + shared elements) and the back-pop (predictive-back: 200 ms in /
 * 100 ms out) transitions. Compose:draw is summed across the journey so the
 * draw-phase delta between variants is directly visible.
 */
@OptIn(ExperimentalMetricApi::class)
@RunWith(AndroidJUnit4::class)
class ComboTransitionBenchmark {

    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun transitionBlurEnabled() = measure(blurEnabled = true)

    @Test
    fun transitionBlurDisabled() = measure(blurEnabled = false)

    private fun measure(blurEnabled: Boolean) = rule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(
            FrameTimingMetric(),
            TraceSectionMetric("Compose:draw", TraceSectionMetric.Mode.Sum),
            MemoryUsageMetric(MemoryUsageMetric.Mode.Max),
        ),
        compilationMode = CompilationMode.None(),
        iterations = 10,
        startupMode = StartupMode.WARM,
        setupBlock = {
            pressHome()
            startActivityWithBlur(blurEnabled)
        },
    ) {
        // 1. Wait for the slot list, then tap the first card.
        device.wait(Until.hasObject(By.res("combo_slot_0")), 5_000)
        device.findObject(By.res("combo_slot_0")).click()

        // 2. Detail screen — wait for the back button, dwell long enough for
        //    the 700 ms transition to settle so all frames get sampled.
        device.wait(Until.hasObject(By.res("combo_back")), 5_000)
        device.waitForIdle()

        // 3. Back-pop and confirm we returned to the slot list.
        device.findObject(By.res("combo_back")).click()
        device.wait(Until.hasObject(By.res("combo_slot_0")), 5_000)
    }
}
