package com.leaguex.app.players

import android.os.Bundle
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.databinding.ActivityJoinTeamBinding

/** 26 Join a team: the invite code from your captain. Join team shows the invite it belongs to. */
class JoinTeamActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityJoinTeamBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.footer)
        b.top.title.setText(R.string.join_title)
        backOn(b.top.back)
        b.code.setText(INVITE_CODE)
        b.join.setOnClickListener { go(TeamInviteActivity::class) }
    }

    private companion object {
        /** Margalla Strikers' invite code. */
        const val INVITE_CODE = "MAR-4821"
    }
}
