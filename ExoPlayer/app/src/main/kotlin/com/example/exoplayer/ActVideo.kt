package com.example.exoplayer

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.exoplayer.databinding.ActVideoBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player

class ActVideo : AppCompatActivity() {

    private lateinit var mBinding: ActVideoBinding

    private var exoPlayer: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActVideoBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initExoPlayer()
    }

    override fun onResume() {
        super.onResume()

        // 자동 실행
        exoPlayer?.let { it.playWhenReady = true }
    }

    override fun onPause() {
        super.onPause()

        if (isPlaying()) {

            exoPlayer?.let { it.playWhenReady = false }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        exoPlayer?.release()
        exoPlayer = null
    }

    private fun isPlaying(): Boolean {

        var result = false
        exoPlayer?.let {

            result = it.playbackState == Player.STATE_READY && it.playWhenReady
        }
        return result
    }

    private fun initExoPlayer() {

        exoPlayer = ExoPlayer.Builder(this@ActVideo).build()

        exoPlayer?.let { player ->

            mBinding.exoPlayer.player = player

            player.addListener(object : Player.Listener {

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)

                    when (playbackState) {

                        Player.STATE_ENDED -> {

                            exoPlayer!!.seekTo(0)
                        }
                    }
                }
            })

            val URL = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
            val uri = Uri.parse(URL)
            val mediaItem = MediaItem.fromUri(uri)

            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        }
    }
}