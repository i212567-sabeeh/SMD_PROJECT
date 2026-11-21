package com.leaguex.app.captains

import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityManageSquadBinding
import com.leaguex.app.kit.keepTogether
import com.leaguex.app.kit.rows.PlayerRow
import com.leaguex.app.kit.rows.PlayerRowAdapter
import com.leaguex.app.kit.rows.Trail
import com.leaguex.app.kit.rows.showRows

/**
 * 29 Manage squad: the captain's view of the squad. Each player's more button opens the player menu; opened with
 * Extras.MENU, the menu shows for Zain Abbas, as the Figma draws it.
 */
class ManageSquadActivity : LeagueXActivity() {

    private lateinit var b: ActivityManageSquadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityManageSquadBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.footer)
        b.top.title.setText(R.string.squad_title)
        backOn(b.top.back)
        b.top.action.isVisible = true
        b.top.action.contentDescription = getString(R.string.row_cd_more)

        val team = SampleData.TEAMS.getValue("MAR")
        val squad = SampleData.SQUADS.getValue("MAR")
        b.intro.text = getString(R.string.squad_intro, team.name, squad.size)
        b.squad.showRows(
            PlayerRowAdapter(
                squad.map { PlayerRow(it.name, it.position, Trail.CallAndMore, it.number) },
                compact = true,
                onTrail = { _, which, button -> if (which == Trail.CallAndMore) playerMenu(button) },
            ),
        )
        b.pending.showRows(
            PlayerRowAdapter(listOf(PlayerRow("Taha Nadeem", getString(R.string.squad_invited_on, keepTogether("Fri 14 Nov")), Trail.Pending)), compact = true),
        )
        b.add.setOnClickListener { go(AddPlayersActivity::class) }

        if (menuToOpen) {
            val zain = squad.indexOfFirst { it.name == "Zain Abbas" }
            b.squad.post {
                b.squad.findViewHolderForAdapterPosition(zain)?.itemView?.findViewById<View>(R.id.more)?.let(::playerMenu)
            }
        }
    }

    /** Change number, Change position and, in red, Remove from team. */
    private fun playerMenu(anchor: View) {
        val popup = PopupMenu(this, anchor, Gravity.END)
        popup.menuInflater.inflate(R.menu.menu_squad_player, popup.menu)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) popup.setForceShowIcon(true)
        popup.menu.findItem(R.id.removePlayer)?.let { item ->
            item.title = SpannableString(item.title).apply {
                setSpan(ForegroundColorSpan(ContextCompat.getColor(this@ManageSquadActivity, R.color.error)), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        popup.setOnMenuItemClickListener { true }
        popup.show()
    }
}
