package com.leaguex.app.news

import android.os.Bundle
import androidx.core.view.isVisible
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.MatchRow
import com.leaguex.app.data.MatchState
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityPlayerBinding
import com.leaguex.app.kit.avatar
import com.leaguex.app.kit.crest
import com.leaguex.app.kit.rows.ResultRowAdapter
import com.leaguex.app.kit.rows.showRows

/** 20 Player page: Zain Abbas's position, team, season numbers and match history. */
class PlayerActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(b.root)
        statusIcons(onBlue = true)
        fitSystemBars(top = b.blueBlock, bottom = b.content)
        backOn(b.top.back)
        b.top.title.setText(R.string.player_title)
        b.top.action.isVisible = true
        b.top.action.contentDescription = getString(R.string.league_cd_share)

        val player = SampleData.SQUADS.getValue("MAR").first { it.name == "Zain Abbas" }
        b.avatar.avatar(player.name)
        b.name.text = player.name
        b.role.text = getString(R.string.player_role, player.position, player.number)
        b.crest.crest("MAR")
        b.team.text = SampleData.teamName("MAR")
        b.played.text = "4"
        b.goals.text = "6"
        b.assists.text = "1"
        b.cards.text = "0"

        b.historyHead.title.setText(R.string.player_history)
        fun goals(n: Int) = if (n == 1) getString(R.string.player_goal_in_match, n) else getString(R.string.player_goals_in_match, n)
        b.history.showRows(
            ResultRowAdapter(
                listOf(
                    MatchRow("MAR", "PIN", MatchState.RESULT, 4, 2, meta = goals(2)),
                    MatchRow("MAR", "POT", MatchState.RESULT, 2, 2, meta = goals(1)),
                    MatchRow("MAR", "SAD", MatchState.RESULT, 3, 1, meta = goals(2)),
                    MatchRow("MAR", "KHY", MatchState.RESULT, 2, 1, meta = goals(1)),
                ),
            ),
        )
    }
}
