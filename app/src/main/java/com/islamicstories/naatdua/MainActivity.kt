package com.islamicstories.naatdua

import android.app.Activity
import android.app.AlertDialog
import android.app.Fragment
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.android.gms.ads.MobileAds

class MainActivity : Activity() {

    private lateinit var tabNaat: TextView
    private lateinit var tabTasbeeh: TextView
    private lateinit var tabPrayer: TextView

    private fun show(frag: Fragment) {
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, frag)
            .commit()
    }

    private fun select(tab: TextView, frag: Fragment) {
        for (t in listOf(tabNaat, tabTasbeeh, tabPrayer)) {
            val active = t == tab
            t.setBackgroundResource(
                if (active) R.drawable.tab_pill_active else R.drawable.tab_pill_idle
            )
            t.setTextColor(
                resources.getColor(if (active) R.color.tab_active_text else R.color.tab_idle_text, null)
            )
        }
        show(frag)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this) {}

        tabNaat = findViewById(R.id.tab_naat)
        tabTasbeeh = findViewById(R.id.tab_tasbeeh)
        tabPrayer = findViewById(R.id.tab_prayer)

        tabNaat.setOnClickListener { select(tabNaat, NaatFragment()) }
        tabTasbeeh.setOnClickListener { select(tabTasbeeh, TasbeehFragment()) }
        tabPrayer.setOnClickListener { select(tabPrayer, NamazFragment()) }

        if (savedInstanceState == null) select(tabNaat, NaatFragment())

        // Needed on Android 13+ to show the playback notification
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_about) {
            AlertDialog.Builder(this)
                .setTitle(R.string.about_title)
                .setMessage(R.string.about_text)
                .setPositiveButton(android.R.string.ok, null)
                .show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
