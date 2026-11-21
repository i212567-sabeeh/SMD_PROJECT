package com.leaguex.app.captains

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import androidx.core.content.getSystemService
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityAddPlayersBinding
import com.leaguex.app.kit.rows.PlayerRow
import com.leaguex.app.kit.rows.PlayerRowAdapter
import com.leaguex.app.kit.rows.Trail
import com.leaguex.app.kit.rows.showRows

/** 30 Add players: forwards with no team yet, to invite, and the team's invite code to share. */
class AddPlayersActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityAddPlayersBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.content)
        b.top.title.setText(R.string.add_title)
        backOn(b.top.back)

        val team = SampleData.TEAMS.getValue("MAR")
        b.city.text = SampleData.ME.city
        b.players.showRows(
            PlayerRowAdapter(
                SampleData.FREE_AGENTS.map { (name, position) ->
                    // Taha Nadeem has already been invited (29 Manage squad lists him under Pending invites).
                    PlayerRow(name, getString(R.string.add_no_team, position), if (name == "Taha Nadeem") Trail.Pending else Trail.Invite)
                },
            ),
        )
        b.shareBody.text = getString(R.string.add_share_body, team.name)
        b.code.text = INVITE_CODE
        b.copy.setOnClickListener {
            getSystemService<ClipboardManager>()?.setPrimaryClip(ClipData.newPlainText(getString(R.string.join_code), INVITE_CODE))
        }
    }

    private companion object {
        const val INVITE_CODE = "MAR-4821"
    }
}
