package com.leaguex.app.organizer

import android.os.Bundle
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.databinding.ActivityCreateTypeBinding

/** 32 Create / Type: a league or a knockout tournament. Continue opens Details for the kind chosen. */
class CreateTypeActivity : LeagueXActivity() {

    private lateinit var b: ActivityCreateTypeBinding
    private var kind = CreateKind.LEAGUE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityCreateTypeBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.footer)
        createTop(b.top, b.steps, step = 1)
        backOn(b.top.back)

        choose(versionOr(CreateKind.LEAGUE))
        b.league.setOnClickListener { choose(CreateKind.LEAGUE) }
        b.leagueRadio.setOnClickListener { choose(CreateKind.LEAGUE) }
        b.knockout.setOnClickListener { choose(CreateKind.TOURNAMENT) }
        b.knockoutRadio.setOnClickListener { choose(CreateKind.TOURNAMENT) }
        b.next.setOnClickListener { go(CreateDetailsActivity::class, kind) }
    }

    private fun choose(chosen: CreateKind) {
        kind = chosen
        val league = chosen == CreateKind.LEAGUE
        b.league.isSelected = league
        b.leagueRadio.isChecked = league
        b.knockout.isSelected = !league
        b.knockoutRadio.isChecked = !league
    }
}
