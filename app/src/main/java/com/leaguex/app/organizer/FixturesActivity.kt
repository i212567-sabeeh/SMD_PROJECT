package com.leaguex.app.organizer

import android.os.Bundle
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.MatchRow
import com.leaguex.app.data.MatchState
import com.leaguex.app.databinding.ActivityFixturesBinding
import com.leaguex.app.kit.keepTogether
import com.leaguex.app.kit.rows.MatchRowAdapter
import com.leaguex.app.kit.rows.showRows

/** 39 Generate fixtures: the season's days, times and courts, with matchday 1 as a preview. */
class FixturesActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityFixturesBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.footer)
        b.top.title.setText(R.string.fixtures_title)
        backOn(b.top.back)

        val courts = resources.getStringArray(R.array.courts)
        b.court1.text = courts[0]
        b.court2.text = courts[1]
        b.start.setText(keepTogether(SEASON_START))
        b.kickoffs.setText(keepTogether("5:00 pm to 8:00 pm"))
        b.preview.text = keepTogether(getString(R.string.fixtures_preview, SEASON_START))
        b.matchdays.text = getString(R.string.fixtures_matchdays, MATCHDAYS)
        b.matches.showRows(
            MatchRowAdapter(
                listOf(
                    MatchRow("MAR", "KHY", MatchState.UPCOMING, time = "5:00 pm", venue = courts[1]),
                    MatchRow("CAP", "PIN", MatchState.UPCOMING, time = "5:00 pm", venue = courts[0]),
                ),
            ),
        )
        b.publish.setOnClickListener { finish() }
    }

    private companion object {
        const val SEASON_START = "Sat 11 Oct"
        const val MATCHDAYS = 14
    }
}
