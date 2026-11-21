package com.leaguex.app.matchday

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityLineupsBinding
import com.leaguex.app.kit.keepTogether
import com.leaguex.app.kit.rows.PlayerRow
import com.leaguex.app.kit.rows.PlayerRowAdapter
import com.leaguex.app.kit.rows.Trail
import com.leaguex.app.kit.rows.showRows

/** 45 Line-ups: tick who plays for each team, one tab per team, then Start match. */
class LineupsActivity : LeagueXActivity() {

    private lateinit var b: ActivityLineupsBinding

    /** The shirt numbers picked for each team: everyone but Margalla's second goalkeeper, as the Figma ticks them. */
    private val picked: Map<String, MutableSet<Int>> = TEAMS.associateWith { code ->
        SampleData.SQUADS.getValue(code).map { it.number }.filterNot { code == "MAR" && it == MAR_LEFT_OUT }.toMutableSet()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLineupsBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.footer)
        b.top.title.setText(R.string.lineups_title)
        backOn(b.top.back)
        b.about.text = keepTogether("MAR vs RAW, ${SampleData.MATCHDAY5_DATE}, 7:00 pm")

        TEAMS.forEach { b.tabs.addTab(b.tabs.newTab().setText(SampleData.teamName(it))) }
        b.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) = show(tab.position)
            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })
        show(0)
        b.start.setOnClickListener { go(RecordMatchActivity::class, RecordMatchVersion.FIRST_HALF) }
    }

    /** One team's squad, each player ticked or not; ticking one updates the counts in the footer. */
    private fun show(tab: Int) {
        val code = TEAMS[tab]
        val chosen = picked.getValue(code)
        val rows = SampleData.SQUADS.getValue(code).map { PlayerRow(it.name, it.position, Trail.Check(it.number in chosen), it.number) }
        b.squad.showRows(
            PlayerRowAdapter(rows, onTrail = pick@{ row, trail, _ ->
                val number = row.number ?: return@pick
                if ((trail as? Trail.Check)?.on == true) chosen += number else chosen -= number
                count(tab)
            }),
        )
        count(tab)
    }

    private fun count(tab: Int) {
        val mine = picked.getValue(TEAMS[tab]).size
        val other = TEAMS[1 - tab]
        b.picked.text = resources.getQuantityString(R.plurals.lineups_picked, mine, mine)
        b.otherPicked.text = getString(R.string.lineups_other_picked, SampleData.teamName(other), picked.getValue(other).size)
    }

    private companion object {
        val TEAMS = listOf("MAR", "RAW")
        const val MAR_LEFT_OUT = 12
    }
}
