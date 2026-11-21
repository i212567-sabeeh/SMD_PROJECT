package com.leaguex.app.league

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import com.google.android.material.tabs.TabLayout
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.EventType
import com.leaguex.app.data.MatchEvent
import com.leaguex.app.data.SampleData
import com.leaguex.app.data.Side
import com.leaguex.app.databinding.ActivityMatchBinding
import com.leaguex.app.kit.ScoreState
import com.leaguex.app.kit.Scoreboard
import com.leaguex.app.kit.StatusKind
import com.leaguex.app.kit.bind
import com.leaguex.app.kit.keepTogether
import com.leaguex.app.kit.rows.EventAdapter
import com.leaguex.app.kit.rows.showRows
import com.leaguex.app.kit.status
import com.leaguex.app.organizer.DashboardActivity

/** 14 Match page's versions, one per Figma frame. */
enum class MatchVersion { BEFORE, LIVE, FULL_TIME, PENALTIES, ORGANIZER }

/** 14 Match page: the scoreboard, then the Timeline, Line-ups or Info tab. A live match's minute ticks over. */
class MatchActivity : LeagueXActivity() {

    private lateinit var b: ActivityMatchBinding
    private lateinit var version: MatchVersion
    private val handler = Handler(Looper.getMainLooper())
    private var minute = LIVE_MINUTE
    private val tick = object : Runnable {
        override fun run() {
            minute++
            b.scoreboard.live.status(StatusKind.LIVE, getString(R.string.match_live, "$minute'"))
            handler.postDelayed(this, MINUTE_MILLIS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMatchBinding.inflate(layoutInflater)
        setContentView(b.root)
        statusIcons(onBlue = true)
        fitSystemBars(top = b.blueBlock, bottom = b.content)
        backOn(b.top.back)
        b.top.action.isVisible = true
        b.top.action.contentDescription = getString(R.string.league_cd_share)

        version = versionOr(MatchVersion.BEFORE)
        val semi = version == MatchVersion.PENALTIES
        b.top.title.text = if (semi) getString(R.string.match_semi_final) else getString(R.string.home_matchday, SampleData.NEXT_MATCHDAY)
        b.scoreboard.bind(
            when (version) {
                MatchVersion.BEFORE, MatchVersion.ORGANIZER -> Scoreboard(
                    "MAR", SampleData.teamName("MAR"), "RAW", SampleData.teamName("RAW"), ScoreState.UPCOMING,
                    keepTogether("7:00 pm"), detail = keepTogether("${SampleData.MATCHDAY5_DATE}, Court 2"),
                )
                MatchVersion.LIVE -> Scoreboard(
                    "MAR", SampleData.teamName("MAR"), "RAW", SampleData.teamName("RAW"), ScoreState.LIVE,
                    getString(R.string.match_live, "$LIVE_MINUTE'"), 2, 1,
                )
                MatchVersion.FULL_TIME -> Scoreboard(
                    "MAR", SampleData.teamName("MAR"), "RAW", SampleData.teamName("RAW"), ScoreState.FULL_TIME,
                    getString(R.string.match_full_time), 3, 2,
                )
                MatchVersion.PENALTIES -> Scoreboard(
                    "MAR", SampleData.teamName("MAR"), "CAP", SampleData.teamName("CAP"), ScoreState.PENALTIES,
                    getString(R.string.match_after_extra_time), 2, 2,
                    detail = getString(R.string.match_win_on_pens, SampleData.teamName("MAR"), 4, 3),
                )
            },
        )

        val venue = if (semi) SEMI_DATE else SampleData.MATCHDAY5_DATE
        b.venue.bind(R.drawable.ic_location_on, "${SampleData.LEAGUE.venue}, Court 2", keepTogether("$venue, 7:00 pm"))
        b.lineUpsNote.bind(R.drawable.ic_checklist, getString(R.string.match_line_ups_not_set), getString(R.string.match_line_ups_body))
        TextViewCompat.setTextAppearance(b.lineUpsNote.title, R.style.TextAppearance_LeagueX_Title_Small)
        b.infoDate.text = keepTogether(venue)
        b.infoTime.text = keepTogether("7:00 pm")
        b.infoVenue.text = "${SampleData.LEAGUE.venue}, Court 2"
        b.infoCompetition.text = if (semi) {
            "${SampleData.CUP.name}, ${getString(R.string.match_semi_final)}"
        } else {
            "${SampleData.LEAGUE.name}, ${getString(R.string.home_matchday, SampleData.NEXT_MATCHDAY)}"
        }
        b.empty.bind(R.drawable.ic_timer, keepTogether(getString(R.string.match_kickoff_at, "7:00 pm")), getString(R.string.match_timeline_empty))

        b.edit.setOnClickListener { go(DashboardActivity::class, sheet = DashboardActivity.SHEET_EDIT_MATCH) }
        val upcoming = version == MatchVersion.BEFORE || version == MatchVersion.ORGANIZER
        b.tabs.getTabAt(if (upcoming) 1 else 0)?.select()
        b.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) = show(tab.position)
            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })
        show(b.tabs.selectedTabPosition)
    }

    override fun onResume() {
        super.onResume()
        if (version == MatchVersion.LIVE) handler.postDelayed(tick, MINUTE_MILLIS)
    }

    override fun onPause() {
        handler.removeCallbacks(tick)
        super.onPause()
    }

    private fun show(tab: Int) {
        listOf(b.organize, b.lineUpsNote.root, b.lineups.root, b.penalties, b.timeline, b.empty.root, b.info, b.venue.root, b.remind)
            .forEach { it.isVisible = false }
        when (tab) {
            TIMELINE -> timeline()
            LINE_UPS -> lineUps()
            else -> {
                b.info.isVisible = true
                b.venue.root.isVisible = false
            }
        }
    }

    private fun timeline() {
        val raw = SampleData.MAR_RAW
        val events: List<MatchEvent> = when (version) {
            MatchVersion.LIVE -> listOf(raw[5], raw[4], raw[3], raw[2], raw[1], raw[0])
            MatchVersion.FULL_TIME -> raw.reversed()
            MatchVersion.PENALTIES -> {
                b.penalties.isVisible = true
                b.kicksHome.bind("MAR", listOf(true, true, false, true, true), 4)
                b.kicksAway.bind("CAP", listOf(true, false, true, true, false), 3)
                val cup = SampleData.CUP_SEMI
                listOf(
                    MatchEvent(null, EventType.PERIOD, Side.HOME, FULL_TIME_NOTE), cup[3], cup[2], cup[1],
                    MatchEvent(null, EventType.PERIOD, Side.HOME, HALF_TIME_NOTE), cup[0],
                )
            }
            else -> {
                b.empty.root.isVisible = true
                return
            }
        }
        b.timeline.isVisible = true
        b.timeline.showRows(EventAdapter(events), divided = false)
    }

    private fun lineUps() {
        val mar = SampleData.SQUADS.getValue("MAR").take(8)
        val away = if (version == MatchVersion.PENALTIES) "CAP" else "RAW"
        if (version == MatchVersion.ORGANIZER) {
            b.organize.isVisible = true
            b.lineUpsNote.root.isVisible = true
        } else {
            b.lineups.root.isVisible = true
            b.lineups.bind("MAR", mar, away, SampleData.SQUADS.getValue(away))
        }
        b.venue.root.isVisible = version == MatchVersion.BEFORE || version == MatchVersion.ORGANIZER
        b.remind.isVisible = version == MatchVersion.BEFORE
    }

    private companion object {
        const val TIMELINE = 0
        const val LINE_UPS = 1
        const val LIVE_MINUTE = 32
        const val MINUTE_MILLIS = 60_000L
        const val SEMI_DATE = "Sat 22 Nov"
        const val FULL_TIME_NOTE = "Full time, 2–2. No goals in extra time"
        const val HALF_TIME_NOTE = "Half time, 1–0"
    }
}
