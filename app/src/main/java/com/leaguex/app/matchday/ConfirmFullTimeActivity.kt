package com.leaguex.app.matchday

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.google.android.material.textview.MaterialTextView
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.EventType
import com.leaguex.app.data.MatchEvent
import com.leaguex.app.data.SampleData
import com.leaguex.app.data.Side
import com.leaguex.app.databinding.ActivityConfirmFullTimeBinding
import com.leaguex.app.kit.crest
import com.leaguex.app.league.MatchActivity
import com.leaguex.app.league.MatchVersion

/**
 * 51 Confirm full time: the final score, each team's scorers, the cards and substitutions, and what finishing does.
 * Finish match answers RESULT_OK, so 46 Record match closes too, and opens the finished match page.
 */
class ConfirmFullTimeActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityConfirmFullTimeBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.footer)
        b.top.title.setText(R.string.confirm_title)
        backOn(b.top.back)

        val goals = SampleData.MAR_RAW.filter { it.type == EventType.GOAL }
        val home = goals.filter { it.side == Side.HOME }
        val away = goals.filter { it.side == Side.AWAY }
        b.homeCrest.crest(HOME)
        b.awayCrest.crest(AWAY)
        b.score.text = getString(R.string.record_score, home.size, away.size)
        scorers(b.homeScorers, b.homeSmallCrest, b.homeName, HOME, home)
        scorers(b.awayScorers, b.awaySmallCrest, b.awayName, AWAY, away)

        b.finish.setOnClickListener {
            setResult(RESULT_OK)
            go(MatchActivity::class, MatchVersion.FULL_TIME)
            finish()
        }
        b.backToRecording.setOnClickListener { finish() }
    }

    /** A team's column: its crest and name, then each scorer and minute ("Zain Abbas 6'"). */
    private fun scorers(column: LinearLayout, crest: TextView, name: TextView, code: String, goals: List<MatchEvent>) {
        crest.crest(code)
        name.text = SampleData.teamName(code)
        for (goal in goals) {
            column.addView(
                MaterialTextView(this).apply {
                    TextViewCompat.setTextAppearance(this, R.style.TextAppearance_LeagueX_Body_Medium)
                    text = getString(R.string.confirm_scorer, goal.player, goal.minute)
                },
            )
        }
    }

    private companion object {
        const val HOME = "MAR"
        const val AWAY = "RAW"
    }
}
