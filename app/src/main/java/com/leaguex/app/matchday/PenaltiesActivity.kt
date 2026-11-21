package com.leaguex.app.matchday

import android.os.Bundle
import androidx.core.view.isVisible
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityPenaltiesBinding
import com.leaguex.app.kit.bind
import com.leaguex.app.kit.crest
import com.leaguex.app.league.MatchActivity
import com.leaguex.app.league.MatchVersion

/**
 * 50 Penalty shootout in the cup semi-final: each team's kicks so far, and the next kick to record as scored or
 * missed. Margalla kick first in each round. Once one team can't be caught, the card names the winner and Finish
 * match opens the match page.
 */
class PenaltiesActivity : LeagueXActivity() {

    private lateinit var b: ActivityPenaltiesBinding

    /** Each team's kicks in order, true when scored: the Figma's 4–3, with Capital's 5th kick still to take. */
    private val home = mutableListOf(true, true, false, true, true)
    private val away = mutableListOf(true, false, true, true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPenaltiesBinding.inflate(layoutInflater)
        setContentView(b.root)
        statusIcons(onBlue = true)
        fitSystemBars(top = b.blueBlock, bottom = b.content)
        backOn(b.top.back)
        b.top.title.setText(R.string.shootout_title)
        b.top.action.isVisible = true
        b.top.action.setIconResource(R.drawable.ic_more_vert)
        b.top.action.contentDescription = getString(R.string.kit_cd_more)
        b.top.action.setOnClickListener { matchMenu(it, penalties = false) }

        b.homeCrest.crest(HOME)
        b.homeCode.text = HOME
        b.awayCrest.crest(AWAY)
        b.awayCode.text = AWAY
        b.scored.setOnClickListener { kick(scored = true) }
        b.missed.setOnClickListener { kick(scored = false) }
        b.undo.setOnClickListener { undo() }
        b.finish.setOnClickListener {
            go(MatchActivity::class, MatchVersion.PENALTIES)
            finish()
        }
        show()
    }

    private fun homeNext() = home.size == away.size

    private fun kick(scored: Boolean) {
        if (Shootout.winner(home, away) != null) return
        (if (homeNext()) home else away) += scored
        show()
    }

    private fun undo() {
        when {
            home.size > away.size -> home.removeAt(home.lastIndex)
            away.isNotEmpty() -> away.removeAt(away.lastIndex)
            else -> return
        }
        show()
    }

    private fun show() {
        val homeGoals = home.count { it }
        val awayGoals = away.count { it }
        b.score.text = getString(R.string.record_score, homeGoals, awayGoals)
        // Five circles each, sliding on a round at a time in sudden death.
        val rounds = maxOf(Shootout.ROUNDS, home.size, away.size)
        val from = rounds - Shootout.ROUNDS
        b.kicksHome.bind(HOME, List(Shootout.ROUNDS) { home.getOrNull(from + it) }, homeGoals)
        b.kicksAway.bind(AWAY, List(Shootout.ROUNDS) { away.getOrNull(from + it) }, awayGoals)

        val won = Shootout.winner(home, away)
        b.kickButtons.isVisible = won == null
        b.finish.isVisible = won != null
        b.undo.isEnabled = home.isNotEmpty()
        if (won != null) {
            b.next.text = getString(R.string.match_win_on_pens, SampleData.teamName(won), maxOf(homeGoals, awayGoals), minOf(homeGoals, awayGoals))
            b.stakes.isVisible = false
            return
        }
        val kicker = if (homeNext()) HOME else AWAY
        val other = if (homeNext()) AWAY else HOME
        val taken = if (homeNext()) home.size else away.size
        b.next.text = getString(R.string.shootout_next, SampleData.teamName(kicker), Shootout.ordinal(taken + 1))
        val ifMissed = if (homeNext()) Shootout.winner(home + false, away) else Shootout.winner(home, away + false)
        val ifScored = if (homeNext()) Shootout.winner(home + true, away) else Shootout.winner(home, away + true)
        val stakes = when {
            ifMissed == other -> getString(R.string.shootout_stakes, SampleData.teamName(other))
            ifScored == kicker -> getString(R.string.shootout_stakes_score, SampleData.teamName(kicker))
            else -> null
        }
        b.stakes.isVisible = stakes != null
        b.stakes.text = stakes
    }

    private companion object {
        const val HOME = Shootout.HOME
        const val AWAY = Shootout.AWAY
    }
}

/** The rules of a shootout between Margalla (who kick first) and Capital: five kicks each, then sudden death. */
internal object Shootout {
    const val HOME = "MAR"
    const val AWAY = "CAP"
    const val ROUNDS = 5

    /**
     * Who has won after [home] and [away]'s kicks (true scored), or null while it goes on. In the first five rounds
     * a team wins once the other can't catch up with the kicks it has left; after that, a round ends it when one
     * team scored and the other didn't.
     */
    fun winner(home: List<Boolean>, away: List<Boolean>): String? {
        val homeGoals = home.count { it }
        val awayGoals = away.count { it }
        if (home.size <= ROUNDS && away.size <= ROUNDS) {
            if (homeGoals > awayGoals + (ROUNDS - away.size)) return HOME
            if (awayGoals > homeGoals + (ROUNDS - home.size)) return AWAY
            if (home.size < ROUNDS || away.size < ROUNDS) return null
        }
        return if (home.size == away.size && homeGoals != awayGoals) (if (homeGoals > awayGoals) HOME else AWAY) else null
    }

    /** "1st", "2nd", "3rd", "4th" … "11th", "21st". */
    fun ordinal(n: Int): String {
        val suffix = if (n % 100 in 11..13) {
            "th"
        } else {
            when (n % 10) {
                1 -> "st"
                2 -> "nd"
                3 -> "rd"
                else -> "th"
            }
        }
        return "$n$suffix"
    }
}
