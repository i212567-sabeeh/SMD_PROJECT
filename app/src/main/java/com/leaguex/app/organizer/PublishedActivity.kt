package com.leaguex.app.organizer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import androidx.core.content.getSystemService
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityPublishedBinding
import com.leaguex.app.screen

/** 36 Published: the league is live; its join code goes to captains. Go to dashboard ends the create flow. */
class PublishedActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityPublishedBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.footer)
        b.top.title.text = null
        b.top.back.setIconResource(R.drawable.ic_close)
        b.top.back.contentDescription = getString(R.string.create_cd_close)
        backOn(b.top.back)

        val league = SampleData.LEAGUE
        b.live.text = getString(R.string.published_live, league.name)
        b.code.text = league.code
        val copy = { _: android.view.View ->
            getSystemService<ClipboardManager>()?.setPrimaryClip(ClipData.newPlainText(getString(R.string.settings_join_code), league.code))
            Unit
        }
        b.copy.setOnClickListener(copy)
        b.copyIcon.setOnClickListener(copy)
        b.dashboard.setOnClickListener {
            startActivity(screen(DashboardActivity::class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }
    }
}
