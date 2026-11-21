package com.leaguex.app.league

import android.os.Bundle
import androidx.core.view.isVisible
import com.google.android.material.tabs.TabLayout
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.Tab
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityStandingsBinding
import com.leaguex.app.kit.StatusKind
import com.leaguex.app.kit.Tie
import com.leaguex.app.kit.bind
import com.leaguex.app.kit.keepTogether
import com.leaguex.app.kit.rows.PlayerRow
import com.leaguex.app.kit.rows.PlayerRowAdapter
import com.leaguex.app.kit.rows.TableAdapter
import com.leaguex.app.kit.rows.Trail
import com.leaguex.app.kit.rows.showRows
import com.leaguex.app.kit.status

/** The Standings frames: 15 Table and 17 Stats (the league), 16 Bracket (the cup). */
enum class StandingsVersion { TABLE, BRACKET, STATS }

/** 15–17 Standings. A league has Table, Playoffs and Stats tabs; a knockout has Bracket and Stats. */
class StandingsActivity : LeagueXActivity() {

    override val tab: Tab get() = Tab.STANDINGS

    private lateinit var b: ActivityStandingsBinding
    private var cup = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityStandingsBinding.inflate(layoutInflater)
        setContentView(b.root)
        statusIcons(onBlue = true)
        fitSystemBars(top = b.blueBlock, bottom = null)
        bottomNav(b.bottomNav.root)
        b.top.title.setText(R.string.standings_title)
        b.league.status.status(StatusKind.SYNCED_ON_BLUE, getString(R.string.home_synced))

        val version = versionOr(StandingsVersion.TABLE)
        cup = version == StandingsVersion.BRACKET
        b.league.leagueName.text = if (cup) SampleData.CUP.name else SampleData.LEAGUE.name
        val tabs = if (cup) {
            listOf(R.string.standings_bracket, R.string.standings_stats)
        } else {
            listOf(R.string.standings_table, R.string.standings_playoffs, R.string.standings_stats)
        }
        tabs.forEach { b.tabs.addTab(b.tabs.newTab().setText(it).setTag(it), false) }
        b.tabs.getTabAt(if (version == StandingsVersion.STATS) 2 else 0)?.select()
        b.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) = show(tab.tag as Int)
            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })
        show(b.tabs.getTabAt(b.tabs.selectedTabPosition)?.tag as Int)
    }

    private fun show(tab: Int) {
        listOf(b.tableArea, b.playoffs.root, b.bracketArea, b.statsArea).forEach { it.isVisible = false }
        when (tab) {
            R.string.standings_table -> {
                b.tableArea.isVisible = true
                b.table.showRows(TableAdapter(SampleData.TABLE, highlight = "MAR", playoffAfter = 4))
            }
            R.string.standings_playoffs -> {
                b.playoffs.root.isVisible = true
                b.playoffs.bind(R.drawable.ic_emoji_events, getString(R.string.standings_playoffs_title), getString(R.string.standings_playoffs_body))
            }
            R.string.standings_bracket -> bracket()
            else -> stats()
        }
    }

    private fun bracket() {
        b.bracketArea.isVisible = true
        val firstRound = keepTogether("Sat 1 Nov")
        b.qf1.bind(Tie("CAP", "KOH", 4, 1, note = firstRound), narrow = true)
        b.qf2.bind(Tie("MAR", "PIN", 3, 0, note = firstRound), narrow = true)
        b.qf3.bind(Tie("SAD", "POT", 2, 1, note = firstRound), narrow = true)
        b.qf4.bind(Tie("RAW", "KHY", 2, 2, note = getString(R.string.standings_pens, "RAW", 5, 4), winner = "RAW"), narrow = true)
        b.sf1.bind(Tie("MAR", "CAP", 2, 2, note = getString(R.string.standings_pens, "MAR", 4, 3), winner = "MAR"), narrow = true)
        b.sf2.bind(Tie("SAD", "RAW", note = keepTogether("Sun 23 Nov")), narrow = true)
        b.finalTie.bind(Tie("MAR", null, note = keepTogether("Sat 29 Nov")), narrow = true)
        b.bracketNote.text = getString(R.string.standings_bracket_note, keepTogether("Sat 29 Nov, 7:00 pm"))
    }

    private fun stats() {
        b.statsArea.isVisible = true
        b.goalsHead.title.setText(R.string.standings_goals)
        b.assistsHead.title.setText(R.string.standings_assists)
        b.cardsHead.title.setText(R.string.standings_cards)
        fun stat(name: String, team: String, n: Int) = PlayerRow(name, SampleData.teamName(team), Trail.Stat(n.toString()))
        fun booked(name: String, team: String, yellow: Int, red: Int) = PlayerRow(name, SampleData.teamName(team), Trail.Cards(yellow, red))
        if (cup) {
            b.goals.showRows(PlayerRowAdapter(listOf(stat("Zain Abbas", "MAR", 3), stat("Danish Iqbal", "CAP", 3)), compact = true))
            b.assists.showRows(PlayerRowAdapter(listOf(stat("Usman Tariq", "MAR", 2), stat("Owais Siddiqui", "RAW", 2)), compact = true))
            b.cards.showRows(PlayerRowAdapter(listOf(booked("Faizan Malik", "MAR", 1, 0), booked("Junaid Akhtar", "POT", 1, 0)), compact = true))
        } else {
            b.goals.showRows(PlayerRowAdapter(listOf(stat("Zain Abbas", "MAR", 6), stat("Danish Iqbal", "CAP", 5)), compact = true))
            b.assists.showRows(PlayerRowAdapter(listOf(stat("Usman Tariq", "MAR", 5), stat("Hamza Ali", "MAR", 4)), compact = true))
            b.cards.showRows(PlayerRowAdapter(listOf(booked("Faizan Malik", "MAR", 2, 0), booked("Waqas Anwar", "RAW", 1, 1)), compact = true))
        }
    }
}
