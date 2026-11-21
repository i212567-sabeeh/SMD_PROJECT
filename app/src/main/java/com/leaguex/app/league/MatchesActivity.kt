package com.leaguex.app.league

import android.os.Bundle
import androidx.core.view.isVisible
import com.google.android.material.tabs.TabLayout
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.Tab
import com.leaguex.app.data.MatchRow
import com.leaguex.app.data.MatchState
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityMatchesBinding
import com.leaguex.app.kit.StatusKind
import com.leaguex.app.kit.bind
import com.leaguex.app.kit.keepTogether
import com.leaguex.app.kit.rows.MatchRowAdapter
import com.leaguex.app.kit.rows.showRows
import com.leaguex.app.kit.status
import com.leaguex.app.news.SearchActivity

/** 13 Matches' versions: the matchdays, and 57 Empty / No matches yet for a league with no fixtures. */
enum class MatchesVersion { DEFAULT, NO_MATCHES }

/** 13 Matches: a matchday's live match and fixtures, one tab per matchday. */
class MatchesActivity : LeagueXActivity() {

    override val tab: Tab get() = Tab.MATCHES

    private lateinit var b: ActivityMatchesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMatchesBinding.inflate(layoutInflater)
        setContentView(b.root)
        statusIcons(onBlue = true)
        fitSystemBars(top = b.blueBlock, bottom = null)
        bottomNav(b.bottomNav.root)
        b.top.title.setText(R.string.matches_title)
        b.top.action.setOnClickListener { go(SearchActivity::class) }
        b.league.status.status(StatusKind.SYNCED_ON_BLUE, getString(R.string.home_synced))
        b.empty.bind(R.drawable.ic_event_busy, getString(R.string.empty_no_matches_title), getString(R.string.empty_no_matches_body))

        if (versionOr(MatchesVersion.DEFAULT) == MatchesVersion.NO_MATCHES) {
            b.league.leagueName.text = OTHER_LEAGUE
            b.tabs.isVisible = false
            showEmpty(true)
            return
        }

        b.league.leagueName.text = SampleData.LEAGUE.name
        for (day in 4..6) b.tabs.addTab(b.tabs.newTab().setText(getString(R.string.matches_matchday, day)), false)
        b.tabs.getTabAt(1)?.select()
        b.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) = matchday(tab.position + 4)
            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })
        b.liveHead.title.setText(R.string.matches_live)
        b.live.showRows(
            MatchRowAdapter(listOf(MatchRow("CAP", "POT", MatchState.LIVE, 2, 1, updated = getString(R.string.matches_updated, "3 min ago")))) {
                go(MatchActivity::class, MatchVersion.LIVE)
            },
        )
        matchday(SampleData.NEXT_MATCHDAY)
    }

    /** Shows [day]'s matches: matchday 5 is tonight, 4 is played, and 6 has no fixtures yet. */
    private fun matchday(day: Int) {
        showEmpty(day > SampleData.NEXT_MATCHDAY)
        b.liveSection.isVisible = day == SampleData.NEXT_MATCHDAY
        val rows = if (day == SampleData.NEXT_MATCHDAY) {
            b.dayHead.title.text = keepTogether(SampleData.MATCHDAY5_DATE)
            listOf(MatchRow("KHY", "KOH", MatchState.RESULT, 1, 1)) + SampleData.MATCHDAY5.filter { it.home == "MAR" || it.home == "SAD" }
        } else {
            b.dayHead.title.text = keepTogether(MATCHDAY4_DATE)
            SampleData.MATCHDAY4
        }
        b.day.showRows(
            MatchRowAdapter(rows) { row ->
                go(MatchActivity::class, if (row.state == MatchState.UPCOMING) MatchVersion.BEFORE else MatchVersion.FULL_TIME)
            },
        )
    }

    private fun showEmpty(empty: Boolean) {
        b.list.isVisible = !empty
        b.emptyArea.isVisible = empty
    }

    private companion object {
        const val MATCHDAY4_DATE = "Sat 8 Nov"

        /** A league whose organizer hasn't published fixtures yet. */
        const val OTHER_LEAGUE = "Lahore Futsal League"
    }
}
