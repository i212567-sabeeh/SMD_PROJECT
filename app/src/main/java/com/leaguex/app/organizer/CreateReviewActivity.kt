package com.leaguex.app.organizer

import android.os.Bundle
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityCreateReviewBinding
import com.leaguex.app.kit.bind

/** 35 Create / Review: everything chosen, each step with Edit, then Publish league. */
class CreateReviewActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityCreateReviewBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.footer)
        createTop(b.top, b.steps, step = 4)
        backOn(b.top.back)

        val league = SampleData.LEAGUE
        val kind = versionOr(CreateKind.LEAGUE)
        b.type.bind(getString(R.string.create_step_type), listOf(getString(R.string.type_league))) { go(CreateTypeActivity::class) }
        b.details.bind(
            getString(R.string.create_step_details),
            listOf("${league.name}, ${league.season}", league.place, league.venue),
        ) { go(CreateDetailsActivity::class, kind) }
        b.rules.bind(
            getString(R.string.create_step_rules),
            listOf("Home and away, 14 matchdays", "Win 3, draw 1, loss 0", getString(R.string.standings_top_four), "2 halves of 20 minutes"),
        ) { go(CreateRulesActivity::class, kind) }
        b.publish.setOnClickListener { go(PublishedActivity::class) }
    }
}
