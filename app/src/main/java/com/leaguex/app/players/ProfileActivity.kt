package com.leaguex.app.players

import android.os.Bundle
import androidx.core.view.isVisible
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.Tab
import com.leaguex.app.data.MatchRow
import com.leaguex.app.data.MatchState
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityProfileBinding
import com.leaguex.app.kit.avatar
import com.leaguex.app.kit.keepTogether
import com.leaguex.app.kit.rows.ActionRow
import com.leaguex.app.kit.rows.ActionRowAdapter
import com.leaguex.app.kit.rows.ResultRowAdapter
import com.leaguex.app.kit.rows.TeamRow
import com.leaguex.app.kit.rows.TeamRowAdapter
import com.leaguex.app.kit.rows.showRows
import com.leaguex.app.league.LeagueActivity
import com.leaguex.app.league.MatchActivity
import com.leaguex.app.league.MatchVersion
import com.leaguex.app.news.TeamActivity
import com.leaguex.app.organizer.CreateTypeActivity
import com.leaguex.app.organizer.DashboardActivity

/** 24 My profile's versions: a player's profile, and an organizer's. */
enum class ProfileVersion { PLAYER, ORGANIZER }

/** 24 My profile: who you are in the app, and what you play in or run. */
class ProfileActivity : LeagueXActivity() {

    override val tab: Tab get() = Tab.PROFILE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(b.root)
        statusIcons(onBlue = true)
        fitSystemBars(top = b.blueBlock, bottom = null)
        bottomNav(b.bottomNav.root)
        b.top.title.setText(R.string.profile_title)
        b.top.action.setIconResource(R.drawable.ic_settings)
        b.top.action.contentDescription = getString(R.string.profile_cd_settings)
        b.edit.setOnClickListener { go(EditProfileActivity::class) }

        if (versionOr(ProfileVersion.PLAYER) == ProfileVersion.ORGANIZER) {
            val organizer = SampleData.LEAGUE.organizer
            b.avatar.avatar(organizer)
            b.name.text = organizer
            b.role.text = getString(R.string.profile_organizer_line, SampleData.ME.city)
            listOf(b.stats, b.teamsSection, b.historySection).forEach { it.isVisible = false }
            b.runSection.isVisible = true
            b.followSection.isVisible = true
            b.runHead.title.setText(R.string.profile_you_run)
            b.run.showRows(
                ActionRowAdapter(
                    listOf(
                        ActionRow(R.drawable.ic_emoji_events, SampleData.LEAGUE.name, "League, 8 teams, matchday 5 of 14"),
                        ActionRow(R.drawable.ic_emoji_events, SampleData.CUP.name, "Knockout, 8 teams, semi-finals"),
                        ActionRow(R.drawable.ic_emoji_events, SampleData.WINTER.name, keepTogether("Knockout, taking entries, starts ${SampleData.WINTER.starts}")),
                    ),
                ) { go(DashboardActivity::class) },
            )
            b.create.setOnClickListener { go(CreateTypeActivity::class) }
            b.followHead.title.setText(R.string.profile_following)
            b.follow.showRows(ActionRowAdapter(listOf(ActionRow(R.drawable.ic_emoji_events, "Lahore Futsal League", "League, 10 teams, Lahore"))) {
                go(LeagueActivity::class)
            })
            return
        }

        val me = SampleData.ME
        b.avatar.avatar(me.name)
        b.name.text = me.name
        b.role.text = getString(R.string.player_role, me.position, me.number)
        b.played.text = "4"
        b.goals.text = "3"
        b.assists.text = "5"
        b.cards.text = "0"
        b.teamsHead.title.setText(R.string.profile_my_teams)
        b.teams.showRows(TeamRowAdapter(listOf(TeamRow(me.team, "${SampleData.LEAGUE.name}, ${SampleData.CUP.name}"))) { go(TeamActivity::class) })
        b.historyHead.title.setText(R.string.player_history)
        b.history.showRows(
            ResultRowAdapter(
                listOf(
                    MatchRow("MAR", "PIN", MatchState.RESULT, 4, 2, meta = getString(R.string.profile_history_line, getString(R.string.profile_one_goal), getString(R.string.profile_assists_count, 2))),
                    MatchRow("MAR", "POT", MatchState.RESULT, 2, 2, meta = getString(R.string.profile_one_assist)),
                ),
            ) { go(MatchActivity::class, MatchVersion.FULL_TIME) },
        )
    }
}
