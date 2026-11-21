package com.leaguex.app.news

import android.os.Bundle
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.Tab
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityTeamsBinding
import com.leaguex.app.kit.StatusKind
import com.leaguex.app.kit.rows.TeamRow
import com.leaguex.app.kit.rows.TeamRowAdapter
import com.leaguex.app.kit.rows.showRows
import com.leaguex.app.kit.status

/** 18 Teams: the followed league's teams in table order. A team opens its page. */
class TeamsActivity : LeagueXActivity() {

    override val tab: Tab get() = Tab.TEAMS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityTeamsBinding.inflate(layoutInflater)
        setContentView(b.root)
        statusIcons(onBlue = true)
        fitSystemBars(top = b.blueBlock, bottom = null)
        bottomNav(b.bottomNav.root)
        b.top.title.setText(R.string.teams_title)
        b.top.action.setOnClickListener { go(SearchActivity::class) }
        b.league.leagueName.text = SampleData.LEAGUE.name
        b.league.status.status(StatusKind.SYNCED_ON_BLUE, getString(R.string.home_synced))
        b.list.showRows(
            TeamRowAdapter(SampleData.TABLE.map { TeamRow(it.code, getString(R.string.teams_meta, SampleData.TEAMS.getValue(it.code).captain, it.p, it.pts)) }) {
                go(TeamActivity::class)
            },
        )
    }
}
