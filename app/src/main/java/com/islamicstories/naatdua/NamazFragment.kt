package com.islamicstories.naatdua

import android.app.Fragment
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

class NamazFragment : Fragment() {

    private var adView: AdView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_namaz, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.btn_namaz).setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.islamicfinder.org/world/pakistan/")
            )
            startActivity(intent)
        }
        adView = view.findViewById<AdView>(R.id.adView)
        adView?.loadAd(AdRequest.Builder().build())
    }

    override fun onPause() {
        super.onPause()
        adView?.pause()
    }

    override fun onResume() {
        super.onResume()
        adView?.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        adView?.destroy()
        adView = null
    }
}
