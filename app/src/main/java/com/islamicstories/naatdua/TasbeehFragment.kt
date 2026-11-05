package com.islamicstories.naatdua

import android.app.Fragment
import android.content.Context
import android.content.res.ColorStateList
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

class TasbeehFragment : Fragment() {

    private lateinit var tvLatin: TextView
    private lateinit var tvCount: TextView
    private lateinit var grid: GridLayout
    private var adView: AdView? = null
    private var toneGen: ToneGenerator? = null

    private var dhikrIndex = 0
    private var count = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_tasbeeh, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val ctx = activity ?: return
        tvLatin = view.findViewById(R.id.tv_dhikr_latin)
        tvCount = view.findViewById(R.id.tv_count)
        grid = view.findViewById(R.id.dhikr_grid)

        // Restore last state
        val prefs = ctx.getSharedPreferences("tasbeeh", Context.MODE_PRIVATE)
        dhikrIndex = prefs.getInt("dhikr", 0).coerceIn(DhikrBank.list.indices)
        count = prefs.getInt("count_$dhikrIndex", 0)

        toneGen = try {
            ToneGenerator(AudioManager.STREAM_MUSIC, 60)
        } catch (e: Exception) {
            null
        }

        view.findViewById<FrameLayout>(R.id.counter_circle).setOnClickListener { tap() }
        view.findViewById<Button>(R.id.btn_reset).setOnClickListener {
            count = 0
            save()
            refresh()
        }

        buildDhikrCards(ctx)
        refresh()

        adView = view.findViewById<AdView>(R.id.adView)
        adView?.loadAd(AdRequest.Builder().build())
    }

    private fun buildDhikrCards(ctx: Context) {
        grid.removeAllViews()
        DhikrBank.list.forEachIndexed { i, d ->
            val card = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setBackgroundResource(R.drawable.dhikr_card)
                backgroundTintList = ColorStateList.valueOf(d.color)
                isClickable = true
                isFocusable = true
                val pad = (14 * resources.displayMetrics.density).toInt()
                setPadding(pad, pad, pad, pad)
                setOnClickListener { selectDhikr(i) }
            }
            val la = TextView(ctx).apply {
                text = d.latin
                setTextColor(0xFFFFFFFF.toInt())
                textSize = 17f
                gravity = Gravity.CENTER
            }
            card.addView(la)
            val lp = GridLayout.LayoutParams().apply {
                width = 0
                columnSpec = GridLayout.spec(i % 2, 1f)
                setMargins(8, 8, 8, 8)
            }
            grid.addView(card, lp)
        }
    }

    private fun selectDhikr(i: Int) {
        dhikrIndex = i
        count = activity?.getSharedPreferences("tasbeeh", Context.MODE_PRIVATE)
            ?.getInt("count_$i", 0) ?: 0
        save()
        refresh()
    }

    private fun tap() {
        count++
        vibrate()
        toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 60)
        save()
        refresh()
    }

    private fun vibrate() {
        val ctx = activity ?: return
        try {
            if (Build.VERSION.SDK_INT >= 31) {
                val vm = ctx.getSystemService(VibratorManager::class.java)
                vm?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val v = ctx.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= 26) {
                    v?.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    v?.vibrate(30)
                }
            }
        } catch (e: Exception) {
            // vibration not available — ignore
        }
    }

    private fun save() {
        activity?.getSharedPreferences("tasbeeh", Context.MODE_PRIVATE)
            ?.edit()
            ?.putInt("dhikr", dhikrIndex)
            ?.putInt("count_$dhikrIndex", count)
            ?.apply()
    }

    private fun refresh() {
        val d = DhikrBank.list[dhikrIndex]
        tvLatin.text = d.latin
        tvCount.text = count.toString()
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
        adView?.destroy()
        adView = null
        toneGen?.release()
        toneGen = null
    }
}
