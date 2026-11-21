package com.leaguex.app.players

import android.os.Bundle
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityTeamInviteBinding
import com.leaguex.app.kit.StatusKind
import com.leaguex.app.kit.crest
import com.leaguex.app.kit.status
import com.leaguex.app.screen

/** 27 Team invite: Hamza Ali's invite to Margalla Strikers. Accept opens your profile; Decline closes the invite. */
class TeamInviteActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityTeamInviteBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.footer)
        b.top.title.setText(R.string.invite_title)
        b.top.back.setIconResource(R.drawable.ic_close)
        b.top.back.contentDescription = getString(R.string.invite_cd_close)
        backOn(b.top.back)

        val team = SampleData.TEAMS.getValue("MAR")
        b.crest.crest(team.code)
        b.headline.text = getString(R.string.invite_headline, team.captain, team.name)
        b.league.status(StatusKind.NEUTRAL, SampleData.LEAGUE.name)
        b.cup.status(StatusKind.NEUTRAL, SampleData.CUP.name)
        b.about.text = getString(R.string.invite_body, SampleData.SQUADS.getValue("MAR").size, SampleData.ME.city)

        b.accept.setOnClickListener { endFlow(JoinTeamActivity::class, screen(ProfileActivity::class, ProfileVersion.PLAYER)) }
        b.decline.setOnClickListener { finish() }
    }
}
