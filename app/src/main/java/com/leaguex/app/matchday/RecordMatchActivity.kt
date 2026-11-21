package com.leaguex.app.matchday

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.EventType
import com.leaguex.app.data.MatchEvent
import com.leaguex.app.data.SampleData
import com.leaguex.app.data.Side
import com.leaguex.app.databinding.ActivityRecordMatchBinding
import com.leaguex.app.databinding.IncludeEventButtonsBinding
import com.leaguex.app.databinding.IncludePlayerPickBinding
import com.leaguex.app.databinding.IncludeTeamPickBinding
import com.leaguex.app.databinding.SheetAddCardBinding
import com.leaguex.app.databinding.SheetAddGoalBinding
import com.leaguex.app.databinding.SheetAddSubBinding
import com.leaguex.app.kit.MatchClock
import com.leaguex.app.kit.StatusKind
import com.leaguex.app.kit.crest
import com.leaguex.app.kit.rows.EventAdapter
import com.leaguex.app.kit.rows.showRows
import com.leaguex.app.kit.showSavedOffline
import com.leaguex.app.kit.showSheet
import com.leaguex.app.kit.status
import com.leaguex.app.screen
import kotlin.math.roundToInt

/** 46 Record match's versions, one per Figma frame. 47–49's sheets open over OFFLINE. */
enum class RecordMatchVersion { FIRST_HALF, HALF_TIME, EXTRA_TIME, OFFLINE }

/**
 * 46 Record match: the score and the match clock on blue, Goal, Card and Sub for each team, and the events so far.
 * The clock ticks every second while it runs. Goal, Card and Sub open 47 Add goal, 48 Add card and 49 Add
 * substitution.
 */
class RecordMatchActivity : LeagueXActivity() {

    /** What one version shows: the away team, the score, the clock and its period, and the events, newest first. */
    private class Recording(
        val away: String,
        val homeGoals: Int,
        val awayGoals: Int,
        val startsAt: Int,
        val periodEnd: Int,
        @get:StringRes val period: Int,
        val running: Boolean,
        val events: List<MatchEvent>,
    )

    private lateinit var b: ActivityRecordMatchBinding
    private lateinit var version: RecordMatchVersion
    private lateinit var recording: Recording
    private var elapsed = 0
    private var running = false
    private val handler = Handler(Looper.getMainLooper())
    private val tick = object : Runnable {
        override fun run() {
            elapsed++
            b.clock.text = MatchClock.text(elapsed, recording.periodEnd)
            handler.postDelayed(this, SECOND_MILLIS)
        }
    }

    /** 51 Confirm full time answers RESULT_OK once the match is finished, and then there's nothing left to record. */
    private val confirm = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRecordMatchBinding.inflate(layoutInflater)
        setContentView(b.root)
        statusIcons(onBlue = true)
        fitSystemBars(top = b.blueBlock, bottom = b.content)
        backOn(b.top.back)
        b.top.title.setText(R.string.record_title)
        b.top.action.isVisible = true
        b.top.action.setIconResource(R.drawable.ic_more_vert)
        b.top.action.contentDescription = getString(R.string.kit_cd_more)
        b.top.action.setOnClickListener { matchMenu(it, penalties = version == RecordMatchVersion.EXTRA_TIME) }

        version = versionOr(RecordMatchVersion.FIRST_HALF)
        recording = recordingFor(version)
        elapsed = recording.startsAt
        running = recording.running

        b.homeCrest.crest(HOME)
        b.homeCode.text = HOME
        b.awayCrest.crest(recording.away)
        b.awayCode.text = recording.away
        b.score.text = getString(R.string.record_score, recording.homeGoals, recording.awayGoals)
        b.clock.text = MatchClock.text(elapsed, recording.periodEnd)
        b.period.setText(recording.period)
        showState()

        val halfTime = version == RecordMatchVersion.HALF_TIME
        val endsMatch = version == RecordMatchVersion.OFFLINE
        b.pause.isVisible = !halfTime
        b.end.isVisible = !halfTime
        b.startSecond.isVisible = halfTime
        if (endsMatch) {
            b.end.setText(R.string.record_end_match)
            b.end.setIconResource(R.drawable.ic_sports_score)
        }
        b.pause.setOnClickListener {
            running = !running
            handler.removeCallbacks(tick)
            if (running) handler.postDelayed(tick, SECOND_MILLIS)
            showState()
        }
        b.end.setOnClickListener {
            if (endsMatch) {
                confirm.launch(screen(ConfirmFullTimeActivity::class))
            } else {
                go(RecordMatchActivity::class, RecordMatchVersion.HALF_TIME)
                finish()
            }
        }
        b.startSecond.setOnClickListener {
            go(RecordMatchActivity::class, RecordMatchVersion.OFFLINE)
            finish()
        }

        b.homeTeam.crest(HOME)
        b.awayTeam.crest(recording.away)
        eventButtons(b.homeEvents, Side.HOME)
        eventButtons(b.awayEvents, Side.AWAY)
        b.eventsHead.title.setText(R.string.record_events)
        b.events.showRows(EventAdapter(recording.events), divided = false)

        if (version == RecordMatchVersion.OFFLINE) {
            b.offline.root.isVisible = true
            b.head.updatePadding(top = dp(OFFLINE_HEAD_TOP))
            b.root.showSavedOffline()
        }
        when (sheetToOpen) {
            SHEET_ADD_GOAL -> goalSheet(Side.HOME)
            SHEET_ADD_CARD -> cardSheet(Side.HOME)
            SHEET_ADD_SUB -> subSheet(Side.HOME)
        }
    }

    override fun onResume() {
        super.onResume()
        if (running) handler.postDelayed(tick, SECOND_MILLIS)
    }

    override fun onPause() {
        handler.removeCallbacks(tick)
        super.onPause()
    }

    /** Running in a live chip with Pause, or Paused on blue with Resume. */
    private fun showState() {
        b.state.status(
            if (running) StatusKind.LIVE else StatusKind.ON_BLUE,
            getString(if (running) R.string.record_running else R.string.record_paused),
        )
        b.pause.setText(if (running) R.string.record_pause else R.string.record_resume)
        b.pause.setIconResource(if (running) R.drawable.ic_pause else R.drawable.ic_play_arrow)
    }

    private fun eventButtons(buttons: IncludeEventButtonsBinding, side: Side) {
        buttons.goal.setOnClickListener { goalSheet(side) }
        buttons.card.setOnClickListener { cardSheet(side) }
        buttons.sub.setOnClickListener { subSheet(side) }
    }

    /** 47 Add goal: the team, scorer, assist, own goal or penalty, and the minute. */
    private fun goalSheet(side: Side) {
        val s = SheetAddGoalBinding.inflate(layoutInflater)
        s.head.sheetTitle.setText(R.string.goal_title)
        s.scorer.label.setText(R.string.goal_scorer)
        s.assist.label.setText(R.string.goal_assist)
        teamPick(s.team, side, s.scorer, s.assist)
        s.minute.value.setText(MatchClock.minute(elapsed, recording.periodEnd))
        val sheet = showSheet(s.root)
        s.save.setOnClickListener { sheet.dismiss() }
    }

    /** 48 Add card: the team, the player, yellow or red, and the minute. */
    private fun cardSheet(side: Side) {
        val s = SheetAddCardBinding.inflate(layoutInflater)
        s.head.sheetTitle.setText(R.string.card_title)
        s.player.label.setText(R.string.card_player)
        teamPick(s.team, side, s.player)
        val pickCard = { yellow: Boolean ->
            s.yellowRadio.isChecked = yellow
            s.redRadio.isChecked = !yellow
        }
        listOf(s.yellow, s.yellowRadio).forEach { view -> view.setOnClickListener { pickCard(true) } }
        listOf(s.red, s.redRadio).forEach { view -> view.setOnClickListener { pickCard(false) } }
        s.minute.value.setText(MatchClock.minute(elapsed, recording.periodEnd))
        val sheet = showSheet(s.root)
        s.save.setOnClickListener { sheet.dismiss() }
    }

    /** 49 Add substitution: the team, who goes off and who comes on, and the minute. */
    private fun subSheet(side: Side) {
        val s = SheetAddSubBinding.inflate(layoutInflater)
        s.head.sheetTitle.setText(R.string.sub_title)
        s.playerOff.label.setText(R.string.sub_off)
        s.playerOn.label.setText(R.string.sub_on)
        teamPick(s.team, side, s.playerOff, s.playerOn)
        s.minute.value.setText(MatchClock.minute(elapsed, recording.periodEnd))
        val sheet = showSheet(s.root)
        s.save.setOnClickListener { sheet.dismiss() }
    }

    /** The team chips, with [side] picked; each of [players] lists the picked team's squad and clears when the team changes. */
    private fun teamPick(team: IncludeTeamPickBinding, side: Side, vararg players: IncludePlayerPickBinding) {
        val showSquad = { code: String ->
            val names = SampleData.SQUADS[code].orEmpty().map { it.name }.toTypedArray()
            players.forEach { field ->
                field.dropdown.setText("", false)
                field.dropdown.setSimpleItems(names)
            }
        }
        team.home.text = SampleData.teamName(HOME)
        team.away.text = SampleData.teamName(recording.away)
        (if (side == Side.HOME) team.home else team.away).isChecked = true
        showSquad(if (side == Side.HOME) HOME else recording.away)
        team.home.setOnCheckedChangeListener { _, on -> if (on) showSquad(HOME) }
        team.away.setOnCheckedChangeListener { _, on -> if (on) showSquad(recording.away) }
    }

    private fun recordingFor(version: RecordMatchVersion): Recording {
        val raw = SampleData.MAR_RAW
        val cup = SampleData.CUP_SEMI
        return when (version) {
            RecordMatchVersion.FIRST_HALF -> Recording("RAW", 1, 1, 1230, 20, R.string.record_first_half_added, true, listOf(raw[1], raw[0]))
            RecordMatchVersion.HALF_TIME -> Recording("RAW", 1, 1, 1200, 20, R.string.record_half_time, false, listOf(raw[2], raw[1], raw[0]))
            RecordMatchVersion.EXTRA_TIME -> Recording(
                "CAP", 2, 2, 2590, 45, R.string.record_extra_first, true,
                listOf(period(EXTRA_TIME_NOTE), cup[3], cup[2], cup[1], period(HALF_TIME_NOTE), cup[0]),
            )
            RecordMatchVersion.OFFLINE -> Recording(
                "RAW", 2, 1, 1985, 40, R.string.record_second_half, true,
                listOf(raw[5].copy(waiting = true), raw[4].copy(waiting = true)),
            )
        }
    }

    private fun period(text: String) = MatchEvent(null, EventType.PERIOD, Side.HOME, text)

    private fun dp(value: Int) = (value * resources.displayMetrics.density).roundToInt()

    companion object {
        const val SHEET_ADD_GOAL = "add_goal"
        const val SHEET_ADD_CARD = "add_card"
        const val SHEET_ADD_SUB = "add_sub"

        private const val HOME = "MAR"
        private const val SECOND_MILLIS = 1_000L
        private const val OFFLINE_HEAD_TOP = 12
        private const val EXTRA_TIME_NOTE = "Full time, 2–2. Extra time"
        private const val HALF_TIME_NOTE = "Half time, 1–0"
    }
}
