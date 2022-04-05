package com.heavymagikhq.lbc_player_ultimate.lbcExoPlayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST
import com.google.android.exoplayer2.ext.cast.CastPlayer
import com.google.android.exoplayer2.ext.cast.SessionAvailabilityListener
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastState
import com.heavymagikhq.lbc_player_ultimate.lbcStreamingService.RadioStreamingService
import com.heavymagikhq.lbc_player_ultimate.lbcStreamingService.RadioStreamingServiceBinder


/**
 * Created by Micah on 13/08/2017.
 */
class LbcExoPlayer(
    val streamUrl: String,
    val context: Context,
    val live: Boolean,
    val view: PlayerView,
    var otherPlayer: LbcExoPlayer? = null
) {
    private var service: RadioStreamingServiceBinder? = null
    private var radioStreamingService: RadioStreamingService? = null
    private var lastTimeStarted = System.currentTimeMillis()
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var released = false
    private var localPlayer: ExoPlayer
    private var castPlayer: CastPlayer
    private var currentPlayer: Player? = null


    // MARK: ---------------- INITIALISATION


    init {
        if (isCastConnected()) {
            otherPlayer?.releaseCast()
        }

        localPlayer = createLocalPlayer()
        castPlayer = createCastPlayer()

        if (isCastConnected()) {
            setCurrentPlayer(castPlayer)
        } else {
            setCurrentPlayer(localPlayer)
        }
    }

    private val connectionToRadioStreamingService: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            if (service is RadioStreamingServiceBinder) {
                radioStreamingService = service.radioStreamingService
                service.radioStreamingService.lbcPlayer = this@LbcExoPlayer
                this@LbcExoPlayer.service = service
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            radioStreamingService = null
        }
    }

    private fun createLocalPlayer(): ExoPlayer {
        released = false

        val player = ExoPlayer.Builder(context)
            .setUseLazyPreparation(true)
            .build()

        player.apply {
            addAnalyticsListener(EventLogger(null))
            experimentalSetOffloadSchedulingEnabled(true)

            repeatMode = if (live) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF

            player.addAnalyticsListener(EventLogger(null))

            addListener(object : Player.Listener {
                override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                    if (playWhenReady) {
                        if (radioStreamingService == null) {
                            startExoPlayerService();
                            context.bindService(
                                Intent(context, RadioStreamingService::class.java),
                                connectionToRadioStreamingService,
                                AppCompatActivity.BIND_AUTO_CREATE
                            )
                        }
                        service?.let {
                            it.radioStreamingService.lbcPlayer = this@LbcExoPlayer
                        }
                        resumeLBC()
                    } else {
                        if (live) {
                            this@LbcExoPlayer.pauseLBC()
                        }
                    }
                }
            })
        }

        return player
    }

    private fun createCastPlayer(): CastPlayer {
        val castPlayer = CastPlayer(CastContext.getSharedInstance(context))

        castPlayer.setSessionAvailabilityListener(object : SessionAvailabilityListener {
            override fun onCastSessionAvailable() {
                if (localPlayer.isPlaying) {
                    setCurrentPlayer(castPlayer)
                }
            }

            override fun onCastSessionUnavailable() {
                setCurrentPlayer(localPlayer)
            }
        })

        castPlayer.addListener(object : Player.Listener {
            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                if (playWhenReady && reason == PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST) {
                    resumeLBC()
                }
            }
        })

        return castPlayer
    }

    private fun isCastConnected() =
        CastContext.getSharedInstance(context).castState == CastState.CONNECTED

    private fun startExoPlayerService() {
        val startExoPlayerServiceIntent = Intent(context, RadioStreamingService::class.java)
        context.startForegroundService(startExoPlayerServiceIntent)
    }

    // MARK: ---------------- INITIALISATION

    // MARK: ---------------- STATE MANAGEMENT

    private fun mediaItem() = MediaItem.Builder()
        .setUri(streamUrl)
        .setMimeType(MimeTypes.BASE_TYPE_AUDIO)
        .build()

    fun release() {
        released = true
        currentPlayer?.clearMediaItems()
        currentPlayer?.stop()
        try {
            radioStreamingService?.let {
                context.unbindService(connectionToRadioStreamingService)
            }
        } catch (e: Throwable) {
            Log.e(this::class.java.name, "LbcExoPlayer - error releasing service: $e")
        }

        context.stopService(Intent(context, RadioStreamingService::class.java))
        currentPlayer?.release()
    }

    @JvmName("setCurrentPlayer1")
    private fun setCurrentPlayer(player: Player, clear: Boolean = false) {
        if (player == currentPlayer) return

        val pos = if (!live) currentPlayer?.currentPosition ?: 0 else 0
        player.setMediaItem(mediaItem(), pos)
        view.player = player


        if (live) {
            currentPlayer?.stop()
            currentPlayer?.clearMediaItems()
        } else {
            currentPlayer?.pause()
        }

        player.playWhenReady = if (!clear) currentPlayer?.playWhenReady ?: false else false
        player.prepare()

        currentPlayer = player
    }

    fun releaseCast() {
        setCurrentPlayer(localPlayer, true)
    }

    // MARK: ---------------- STATE MANAGEMENT

    // MARK: ---------------- CONTROLS

    /**
     * makes the exoPlayer resume playing from its last position
     */
    fun resumeLBC() {
        if (isCastConnected()) {
            otherPlayer?.releaseCast()
        }
        setCurrentPlayer(if (isCastConnected()) castPlayer else localPlayer)

        if (released) {
            localPlayer = createLocalPlayer()
            castPlayer = createCastPlayer()
            return
        }

        handler.removeCallbacksAndMessages(null)
        lastTimeStarted = System.currentTimeMillis()
        currentPlayer?.apply {
            if (live) {
                stop()
                clearMediaItems()
                setMediaItem(mediaItem(), 0)
            }
            if (!live) {
                val pos = currentPlayer?.currentPosition ?: 0
                currentPlayer?.seekTo(pos)
            }
            prepare()
            play()
        }
    }

    fun pauseLBC() {
        currentPlayer?.pause()
        handler.postDelayed(::release, 18000000)
    }

    // MARK: ---------------- CONTROLS
}