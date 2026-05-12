package com.kartollika.mobiussharedtransitions.combo.video

import android.net.Uri
import android.view.LayoutInflater
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.kartollika.mobiussharedtransitions.R
import com.kartollika.mobiussharedtransitions.combo.blur.ComboBlurZIndex
import com.kartollika.mobiussharedtransitions.combo.blur.LocalBlurProvider
import com.kartollika.mobiussharedtransitions.combo.blur.backgroundBlurSource

private val BackgroundSurfaceColor = Color(0xFF68737D)

@OptIn(UnstableApi::class)
@Composable
fun ComboVideo(
    videoUri: Uri,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val blurProvider = LocalBlurProvider.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            repeatMode = Player.REPEAT_MODE_ONE
            videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            playWhenReady = false
            prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    AndroidView(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundSurfaceColor)
            .backgroundBlurSource(blurProvider, zIndex = ComboBlurZIndex.Video),
        factory = { ctx ->
            val playerView = LayoutInflater.from(ctx)
                .inflate(R.layout.combo_player_view, null) as PlayerView
            playerView.apply {
                setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                setKeepContentOnPlayerReset(true)
                player = exoPlayer
            }
        },
    )
}
