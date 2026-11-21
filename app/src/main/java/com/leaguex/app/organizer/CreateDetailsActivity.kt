package com.leaguex.app.organizer

import android.os.Bundle
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityCreateDetailsBinding

/** 33 Create / Details: name, season, banner and where it's played. Continue opens the kind's Rules. */
class CreateDetailsActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityCreateDetailsBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.footer)
        createTop(b.top, b.steps, step = 2)
        backOn(b.top.back)

        val league = SampleData.LEAGUE
        b.name.setText(league.name)
        b.season.setText(league.season.substringAfter(' '))
        b.province.setText(SampleData.ME.province, false)
        b.city.setText(SampleData.ME.city, false)
        b.venue.setText(league.venue)
        val kind = versionOr(CreateKind.LEAGUE)
        b.next.setOnClickListener { go(CreateRulesActivity::class, kind) }
    }
}
