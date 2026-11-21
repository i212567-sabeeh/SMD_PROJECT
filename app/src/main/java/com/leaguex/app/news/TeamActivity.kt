package com.leaguex.app.news

import android.os.Bundle
import androidx.core.view.isVisible
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.MatchRow
import com.leaguex.app.data.MatchState
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityTeamBinding
import com.leaguex.app.kit.StatusKind
import com.leaguex.app.kit.crest
import com.leaguex.app.kit.rows.PlayerRow
import com.leaguex.app.kit.rows.PlayerRowAdapter
import com.leaguex.app.kit.rows.ResultRowAdapter
import com.leaguex.app.kit.rows.Trail
import com.leaguex.app.kit.rows.showRows
import com.leaguex.app.kit.status

/** 19 Team page's versions: a spectator's view, the captain's, and 57 Loading / Images while photos load. */
enum class TeamVersion { DEFAULT, CAPTAIN, LOADING }

/** 19 Team page: Margalla Strikers' crest, captain, competitions, squad and results. */
class TeamActivity : LeagueXActivity() {

    private lateinit var b: ActivityTeamBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityTeamBinding.inflate(layoutInflater)
        setContentView(b.root)
        statusIcons(onBlue = true)
        fitSystemBars(top = b.blueBlock, bottom = b.content)
        backOn(b.top.back)
        b.top.title.setText(R.string.team_title)
        b.top.action.isVisible = true
        b.top.action.contentDescription = getString(R.string.league_cd_share)

        val team = SampleData.TEAMS.getValue("MAR")
        b.name.text = team.name
        b.squadHead.title.setText(R.string.team_squad)
        val squad = SampleData.SQUADS.getValue("MAR")

        val version = versionOr(TeamVersion.DEFAULT)
        if (version == TeamVersion.LOADING) {
            b.crest.isVisible = false
            b.crestLoading.isVisible = true
            b.about.setText(R.string.team_loading)
            b.competitions.isVisible = false
            b.resultsSection.isVisible = false
            b.content.dividerDrawable = getDrawable(R.drawable.gap_12)
            b.squad.showRows(PlayerRowAdapter(squad.take(5).map { PlayerRow(it.name, it.position, Trail.None) }, skeleton = true))
            return
        }

        b.crest.crest(team.code)
        b.about.text = getString(R.string.team_captain_place, team.captain, SampleData.ME.city)
        b.league.status(StatusKind.ON_BLUE, SampleData.LEAGUE.name)
        b.cup.status(StatusKind.ON_BLUE, SampleData.CUP.name)
        b.captainActions.isVisible = version == TeamVersion.CAPTAIN

        // "See all 9" shows the rest of the squad.
        val shown = if (version == TeamVersion.CAPTAIN) 3 else 4
        showSquad(squad.take(shown))
        b.squadHead.action.isVisible = true
        b.squadHead.action.text = getString(R.string.team_see_all, squad.size)
        b.squadHead.action.setOnClickListener {
            showSquad(squad)
            b.squadHead.action.isVisible = false
        }

        b.resultsHead.title.setText(R.string.team_results)
        b.results.showRows(
            ResultRowAdapter(
                listOf(
                    MatchRow("MAR", "PIN", MatchState.RESULT, 4, 2, meta = getString(R.string.team_result_league)),
                    MatchRow("MAR", "CAP", MatchState.RESULT, 2, 2, meta = getString(R.string.team_result_cup_pens)),
                ),
            ),
        )
    }

    private fun showSquad(players: List<com.leaguex.app.data.SquadPlayer>) {
        b.squad.showRows(PlayerRowAdapter(players.map { PlayerRow(it.name, it.position, number = it.number) }) { go(PlayerActivity::class) })
    }
}
