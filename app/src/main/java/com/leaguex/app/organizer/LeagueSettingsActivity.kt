package com.leaguex.app.organizer

import android.os.Bundle
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityLeagueSettingsBinding
import com.leaguex.app.kit.bind

/** 44 League settings: each setting's current value, and End season, which leads to the playoffs. */
class LeagueSettingsActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityLeagueSettingsBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.content)
        b.top.title.setText(R.string.settings_league_title)
        backOn(b.top.back)

        val league = SampleData.LEAGUE
        b.details.bind(R.drawable.ic_edit, R.string.settings_details, "${league.name}, ${league.season}") { go(CreateDetailsActivity::class) }
        b.rules.bind(R.drawable.ic_rule, R.string.settings_rules, "Home and away, top 4 playoffs") { go(CreateRulesActivity::class) }
        b.banner.bind(R.drawable.ic_image, R.string.settings_banner, "Blue pitch")
        b.joinCode.bind(R.drawable.ic_key, R.string.settings_join_code, league.code)
        b.organizers.bind(R.drawable.ic_group, R.string.settings_organizers, league.organizer)
        b.endSeason.setOnClickListener { go(StartPlayoffsActivity::class) }
    }
}
