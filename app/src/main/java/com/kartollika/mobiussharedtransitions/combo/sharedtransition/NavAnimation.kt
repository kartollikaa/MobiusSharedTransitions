package com.kartollika.mobiussharedtransitions.combo.sharedtransition

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.unit.Dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope

/**
 * Animates a [Float] driven by the current [LocalNavAnimatedContentScope]'s enter/exit
 * transition: holds [visible] while the destination is on screen and [hidden] before
 * enter or after exit.
 */
@Composable
fun animateNavEnterExitFloat(visible: Float, hidden: Float): State<Float> =
    LocalNavAnimatedContentScope.current.transition.animateFloat { state ->
        when (state) {
            EnterExitState.Visible -> visible
            EnterExitState.PreEnter, EnterExitState.PostExit -> hidden
        }
    }

/**
 * [Dp] counterpart of [animateNavEnterExitFloat].
 */
@Composable
fun animateNavEnterExitDp(visible: Dp, hidden: Dp): State<Dp> =
    LocalNavAnimatedContentScope.current.transition.animateDp { state ->
        when (state) {
            EnterExitState.Visible -> visible
            EnterExitState.PreEnter, EnterExitState.PostExit -> hidden
        }
    }
