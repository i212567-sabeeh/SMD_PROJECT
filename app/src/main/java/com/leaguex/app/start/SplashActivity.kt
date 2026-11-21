package com.leaguex.app.start

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.databinding.ActivitySplashBinding

/** 01 Splash's versions: FLOW moves on to Welcome by itself; STILL (the Figma frame) waits for a tap. */
enum class SplashVersion { FLOW, STILL }

/** 01 Splash: the wordmark on blue. Where "Start the app" begins. */
class SplashActivity : LeagueXActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private val moveOn = Runnable { welcome() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusIcons(onBlue = true)
        fitSystemBars(top = binding.root, bottom = null)
        binding.root.setOnClickListener { welcome() }
        if (versionOr(SplashVersion.FLOW) == SplashVersion.FLOW) handler.postDelayed(moveOn, SPLASH_MILLIS)
    }

    override fun onDestroy() {
        handler.removeCallbacks(moveOn)
        super.onDestroy()
    }

    private fun welcome() {
        handler.removeCallbacks(moveOn)
        go(WelcomeActivity::class)
        finish()
    }

    private companion object {
        const val SPLASH_MILLIS = 1500L
    }
}
