package com.islamicstories.naatdua

import android.app.Fragment
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

class NaatFragment : Fragment() {

    private var service: PlaybackService? = null
    private var bound = false
    private val handler = Handler(Looper.getMainLooper())
    private var adView: AdView? = null
    private lateinit var seekBar: SeekBar
    private lateinit var btnPlay: Button
    private lateinit var tvTime: TextView

    private val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, b: IBinder?) {
            service = (b as PlaybackService.LocalBinder).getService()
            bound = true
            syncUi()
            handler.post(updater)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
            service = null
        }
    }

    private val updater = object : Runnable {
        override fun run() {
            val s = service
            if (bound && s != null) {
                if (s.isPlaying()) {
                    seekBar.progress = s.position()
                }
                tvTime.text = fmt(s.position()) + " / " + fmt(s.duration())
                if (::btnPlay.isInitialized) {
                    btnPlay.text = getString(if (s.isPlaying()) R.string.pause else R.string.play)
                }
                handler.postDelayed(this, 500)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_naat, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        seekBar = view.findViewById(R.id.seekbar)
        btnPlay = view.findViewById(R.id.btn_play)
        tvTime = view.findViewById(R.id.tv_time)

        adView = view.findViewById<AdView>(R.id.adView)
        adView?.loadAd(AdRequest.Builder().build())

        btnPlay.setOnClickListener {
            val ctx = activity ?: return@setOnClickListener
            // Start the service so playback survives leaving the app
            ctx.startService(Intent(ctx, PlaybackService::class.java))
            service?.toggle() ?: run {
                // not bound yet — toggle via intent
                ctx.startService(
                    Intent(ctx, PlaybackService::class.java)
                        .setAction(PlaybackService.ACTION_TOGGLE)
                )
            }
        }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) service?.seekTo(progress)
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
    }

    override fun onStart() {
        super.onStart()
        val ctx = activity ?: return
        ctx.bindService(
            Intent(ctx, PlaybackService::class.java), conn, Context.BIND_AUTO_CREATE
        )
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(updater)
        if (bound) {
            activity?.unbindService(conn)
            bound = false
            service = null
        }
    }

    private fun syncUi() {
        val s = service ?: return
        seekBar.max = s.duration().takeIf { it > 0 } ?: 1
        tvTime.text = fmt(s.position()) + " / " + fmt(s.duration())
        btnPlay.text = getString(if (s.isPlaying()) R.string.pause else R.string.play)
    }

    private fun fmt(ms: Int): String {
        val s = ms / 1000
        return String.format("%d:%02d", s / 60, s % 60)
    }

    override fun onPause() {
        super.onPause()
        adView?.pause()
    }

    override fun onResume() {
        super.onResume()
        adView?.resume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updater)
        adView?.destroy()
        adView = null
    }
}
