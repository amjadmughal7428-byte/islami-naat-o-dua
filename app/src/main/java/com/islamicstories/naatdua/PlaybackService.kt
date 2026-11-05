package com.islamicstories.naatdua

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder

/**
 * Foreground service that owns the naat MediaPlayer, so audio keeps playing
 * when the user leaves the app. The player is released when the service
 * is destroyed — no leaks.
 */
class PlaybackService : Service() {

    companion object {
        const val ACTION_TOGGLE = "com.islamicstories.naatdua.TOGGLE"
        const val ACTION_STOP = "com.islamicstories.naatdua.STOP"
        private const val CHANNEL_ID = "naat_playback"
        private const val NOTIF_ID = 1
    }

    private var player: MediaPlayer? = null
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): PlaybackService = this@PlaybackService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_TOGGLE -> toggle()
            ACTION_STOP -> {
                pause()
                stopForeground(true)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun ensurePlayer(): MediaPlayer {
        var p = player
        if (p == null) {
            p = MediaPlayer.create(this, R.raw.maula_ya_salli_duff)
            p.setOnCompletionListener {
                pause()
                stopForeground(true)
            }
            player = p
        }
        return p
    }

    fun toggle() {
        val p = ensurePlayer()
        if (p.isPlaying) pause() else play()
    }

    fun play() {
        val p = ensurePlayer()
        if (!p.isPlaying) {
            p.start()
            startForeground(NOTIF_ID, buildNotification(true))
        }
    }

    fun pause() {
        player?.let { if (it.isPlaying) it.pause() }
        stopForeground(true)
    }

    fun isPlaying(): Boolean = player?.isPlaying == true
    fun position(): Int = player?.currentPosition ?: 0
    fun duration(): Int = player?.duration ?: 0
    fun seekTo(ms: Int) { player?.seekTo(ms) }

    private fun buildNotification(playing: Boolean): Notification {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) {
            nm.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Naat playback", NotificationManager.IMPORTANCE_LOW)
            )
        }
        val toggleIntent = PendingIntent.getService(
            this, 0, Intent(this, PlaybackService::class.java).setAction(ACTION_TOGGLE),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val stopIntent = PendingIntent.getService(
            this, 1, Intent(this, PlaybackService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val builder = if (Build.VERSION.SDK_INT >= 26) {
            Notification.Builder(this, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
        }
        return builder
            .setContentTitle("Islami Naat o Dua")
            .setContentText(if (playing) "Playing naat…" else "Naat paused")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .addAction(android.R.drawable.ic_media_pause, if (playing) "Pause" else "Play", toggleIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopIntent)
            .setOngoing(playing)
            .build()
    }

    override fun onDestroy() {
        player?.release()
        player = null
        super.onDestroy()
    }
}
