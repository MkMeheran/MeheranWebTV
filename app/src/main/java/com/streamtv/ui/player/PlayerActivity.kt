package com.streamtv.ui.player

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.streamtv.data.ChannelRepository
import com.streamtv.model.Channel
import com.streamtv.util.NumberInputHandler
import kotlinx.coroutines.launch

class PlayerActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var overlayContainer: View
    private lateinit var tvOverlayChannelNo: TextView
    private lateinit var tvOverlayChannelName: TextView
    private lateinit var tvOverlayCategory: TextView
    private lateinit var tvNumberInput: TextView
    private lateinit var errorOverlay: View
    private lateinit var btnRetry: Button
    private lateinit var btnBack: Button

    private val repository = ChannelRepository()
    private var allChannels: List<Channel> = emptyList()
    private var currentChannel: Channel? = null
    
    private val overlayHandler = Handler(Looper.getMainLooper())
    private val hideOverlayRunnable = Runnable { overlayContainer.visibility = View.GONE }
    private lateinit var numberInputHandler: NumberInputHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(resources.getIdentifier("activity_player", "layout", packageName))

        playerView = findViewById(resources.getIdentifier("playerView", "id", packageName))
        overlayContainer = findViewById(resources.getIdentifier("overlayContainer", "id", packageName))
        tvOverlayChannelNo = findViewById(resources.getIdentifier("tvOverlayChannelNo", "id", packageName))
        tvOverlayChannelName = findViewById(resources.getIdentifier("tvOverlayChannelName", "id", packageName))
        tvOverlayCategory = findViewById(resources.getIdentifier("tvOverlayCategory", "id", packageName))
        tvNumberInput = findViewById(resources.getIdentifier("tvNumberInput", "id", packageName))
        errorOverlay = findViewById(resources.getIdentifier("errorOverlay", "id", packageName))
        btnRetry = findViewById(resources.getIdentifier("btnRetry", "id", packageName))
        btnBack = findViewById(resources.getIdentifier("btnBack", "id", packageName))

        numberInputHandler = NumberInputHandler(
            onChannelSelected = { channelNo ->
                val nextChannel = allChannels.find { it.serial_no == channelNo }
                if (nextChannel != null) {
                    playChannel(nextChannel)
                }
            },
            onUpdateUI = { text ->
                if (text.isEmpty()) {
                    tvNumberInput.visibility = View.GONE
                } else {
                    tvNumberInput.text = text
                    tvNumberInput.visibility = View.VISIBLE
                }
            }
        )

        btnRetry.setOnClickListener {
            errorOverlay.visibility = View.GONE
            currentChannel?.let { playChannel(it) }
        }

        btnBack.setOnClickListener {
            finish()
        }

        lifecycleScope.launch {
            allChannels = repository.getChannels()
            val initialNo = intent.getIntExtra("channel_no", 1)
            currentChannel = allChannels.find { it.serial_no == initialNo } ?: allChannels.firstOrNull()
            currentChannel?.let { playChannel(it) }
        }
    }

    private fun playChannel(channel: Channel) {
        currentChannel = channel
        tvOverlayChannelNo.text = "CH ${channel.serial_no}"
        tvOverlayChannelName.text = channel.name
        tvOverlayCategory.text = channel.category?.uppercase() ?: "ALL"
        
        showOverlayTemporarily()

        if (player == null) {
            player = ExoPlayer.Builder(this).build()
            playerView.player = player
            player?.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    val progressBar = findViewById<View>(resources.getIdentifier("progressBar", "id", packageName))
                    if (playbackState == Player.STATE_BUFFERING) {
                        progressBar.visibility = View.VISIBLE
                    } else {
                        progressBar.visibility = View.GONE
                    }
                }
                override fun onPlayerError(error: PlaybackException) {
                    errorOverlay.visibility = View.VISIBLE
                    val progressBar = findViewById<View>(resources.getIdentifier("progressBar", "id", packageName))
                    progressBar.visibility = View.GONE
                }
            })
        }
        
        val mediaItem = MediaItem.fromUri(Uri.parse(channel.stream_url))
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true
    }

    private fun showOverlayTemporarily() {
        overlayContainer.visibility = View.VISIBLE
        overlayHandler.removeCallbacks(hideOverlayRunnable)
        overlayHandler.postDelayed(hideOverlayRunnable, 3500)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when {
            keyCode in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9 -> {
                numberInputHandler.handleInput(keyCode - KeyEvent.KEYCODE_0)
                return true
            }
            keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER -> {
                if (overlayContainer.visibility == View.VISIBLE) {
                    overlayContainer.visibility = View.GONE
                } else {
                    showOverlayTemporarily()
                }
                return true
            }
            keyCode == KeyEvent.KEYCODE_CHANNEL_UP || keyCode == KeyEvent.KEYCODE_DPAD_UP -> {
                switchChannel(1)
                return true
            }
            keyCode == KeyEvent.KEYCODE_CHANNEL_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_DOWN -> {
                switchChannel(-1)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun switchChannel(direction: Int) {
        if (allChannels.isEmpty()) return
        val currentIndex = allChannels.indexOf(currentChannel)
        if (currentIndex != -1) {
            var nextIndex = currentIndex + direction
            if (nextIndex >= allChannels.size) nextIndex = 0
            if (nextIndex < 0) nextIndex = allChannels.size - 1
            playChannel(allChannels[nextIndex])
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        overlayHandler.removeCallbacks(hideOverlayRunnable)
    }
}
