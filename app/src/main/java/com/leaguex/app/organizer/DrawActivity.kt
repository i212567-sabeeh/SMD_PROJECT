package com.leaguex.app.organizer

import android.os.Bundle
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityDrawBinding
import com.leaguex.app.kit.bind
import com.leaguex.app.league.StandingsActivity
import com.leaguex.app.league.StandingsVersion

/** 40 Draw: the Capital Futsal Cup's quarter-finals. Draw again reshuffles them; Confirm draw shows the bracket. */
class DrawActivity : LeagueXActivity() {

    private lateinit var b: ActivityDrawBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDrawBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.footer)
        b.top.title.setText(R.string.draw_title)
        backOn(b.top.back)

        b.about.text = getString(R.string.draw_about, SampleData.CUP.name, SampleData.TEAMS.size)
        b.quartersHead.title.setText(R.string.standings_quarters)
        show(DRAWN)
        b.again.setOnClickListener { show(DRAWN.map { it.reversed() }.shuffled()) }
        b.confirm.setOnClickListener { go(StandingsActivity::class, StandingsVersion.BRACKET) }
    }

    private fun show(pairs: List<List<String>>) {
        listOf(b.qf1, b.qf2, b.qf3, b.qf4).forEachIndexed { i, row -> row.bind("QF${i + 1}", pairs[i][0], pairs[i][1]) }
    }

    private companion object {
        /** The draw the Figma shows. */
        val DRAWN = listOf(listOf("CAP", "KOH"), listOf("MAR", "PIN"), listOf("SAD", "POT"), listOf("RAW", "KHY"))
    }
}
