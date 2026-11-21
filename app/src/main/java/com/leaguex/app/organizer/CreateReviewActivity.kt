package com.leaguex.app.organizer

import android.os.Bundle
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityCreateReviewBinding
import com.leaguex.app.kit.bind

/** 35 Create / Review: everything chosen, each step with Edit, then Publish league (or tournament). */
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
        val type = if (kind == CreateKind.TOURNAMENT) R.string.type_knockout else R.string.type_league
        b.type.bind(getString(R.string.create_step_type), listOf(getString(type))) { returnTo(CreateTypeActivity::class, kind) }
        b.details.bind(
            getString(R.string.create_step_details),
            listOf("${league.name}, ${league.season}", league.place, league.venue),
        ) { returnTo(CreateDetailsActivity::class, kind) }
        b.rules.bind(
            getString(R.string.create_step_rules),
            listOf("Home and away, 14 matchdays", "Win 3, draw 1, loss 0", getString(R.string.standings_top_four), "2 halves of 20 minutes"),
        ) { returnTo(CreateRulesActivity::class, kind) }
        b.publish.setText(if (kind == CreateKind.TOURNAMENT) R.string.review_publish_tournament else R.string.review_publish)
        b.publish.setOnClickListener { go(PublishedActivity::class, kind) }
    }
}
