package com.leaguex.app.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.leaguex.app.Extras
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.Tab
import com.leaguex.app.across.opensSyncStatus
import com.leaguex.app.data.MatchRow
import com.leaguex.app.data.MatchState
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityHomeBinding
import com.leaguex.app.databinding.ItemSheetOptionBinding
import com.leaguex.app.databinding.SheetSwitchLeagueBinding
import com.leaguex.app.kit.StatusKind
import com.leaguex.app.kit.Tie
import com.leaguex.app.kit.bind
import com.leaguex.app.kit.keepTogether
import com.leaguex.app.kit.rows.ActionRow
import com.leaguex.app.kit.rows.ActionRowAdapter
import com.leaguex.app.kit.rows.EventAdapter
import com.leaguex.app.kit.rows.MatchRowAdapter
import com.leaguex.app.kit.rows.ResultRowAdapter
import com.leaguex.app.kit.rows.TableAdapter
import com.leaguex.app.kit.rows.showRows
import com.leaguex.app.kit.showSheet
import com.leaguex.app.kit.status
import com.leaguex.app.league.MatchActivity
import com.leaguex.app.league.MatchVersion
import com.leaguex.app.league.StandingsActivity
import com.leaguex.app.league.StandingsVersion
import com.leaguex.app.matchday.LineupsActivity
import com.leaguex.app.matchday.RecordMatchActivity
import com.leaguex.app.matchday.RecordMatchVersion
import com.leaguex.app.news.AnnouncementActivity
import com.leaguex.app.news.NotificationsActivity
import com.leaguex.app.news.SearchActivity
import com.leaguex.app.organizer.DashboardActivity
import com.leaguex.app.organizer.TeamRequestsActivity
import kotlin.math.roundToInt

/** 08 Home's versions, one per Figma frame. */
enum class HomeVersion { LEAGUE, TOURNAMENT, LIVE, NOTHING, OFFLINE, ORGANIZER }

/** 08 Home: the followed competition's match that matters now, and the lists around it. */
class HomeActivity : LeagueXActivity() {

    override val tab: Tab get() = Tab.HOME

    private lateinit var b: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(b.root)
        statusIcons(onBlue = true)
        fitSystemBars(top = b.blueBlock, bottom = null)
        bottomNav(b.bottomNav.root)

        val version = versionOr(HomeVersion.LEAGUE)
        listOf(
            b.tableSection, b.resultsSection, b.semisSection, b.quartersSection, b.eventsSection, b.earlierSection,
            b.popularSection, b.attentionSection, b.recordedSection,
        ).forEach { it.isVisible = false }
        when (version) {
            HomeVersion.LEAGUE -> league()
            HomeVersion.TOURNAMENT -> tournament()
            HomeVersion.LIVE -> live()
            HomeVersion.NOTHING -> nothingFollowed()
            HomeVersion.OFFLINE -> offline()
            HomeVersion.ORGANIZER -> organizer()
        }

        // Taps: the league name opens 09 Switch league; the hero's buttons open the match or the bracket.
        val switch = View.OnClickListener { switchLeague(version) }
        b.league.leagueName.setOnClickListener(switch)
        b.league.switchArrow.setOnClickListener(switch)
        b.primary.setOnClickListener {
            when (version) {
                HomeVersion.LIVE -> go(MatchActivity::class, MatchVersion.LIVE)
                HomeVersion.TOURNAMENT -> go(StandingsActivity::class, StandingsVersion.BRACKET)
                HomeVersion.ORGANIZER -> go(RecordMatchActivity::class, RecordMatchVersion.FIRST_HALF)
                HomeVersion.NOTHING -> Unit
                else -> go(MatchActivity::class, MatchVersion.BEFORE)
            }
        }
        b.top.search.setOnClickListener { go(SearchActivity::class) }
        b.top.notifications.setOnClickListener { go(NotificationsActivity::class) }
        b.notice.setOnClickListener { go(AnnouncementActivity::class) }
        b.findLeagues.setOnClickListener { go(SearchActivity::class) }
        if (version == HomeVersion.ORGANIZER) b.secondary.setOnClickListener { go(DashboardActivity::class) }
        if (version == HomeVersion.LIVE) b.secondary.setOnClickListener { go(MatchActivity::class, MatchVersion.LIVE) }
        if (sheetToOpen == SHEET_SWITCH_LEAGUE) switchLeague(version)
    }

    /** Home is single-top: choosing another competition in 09 Switch league reopens it in that version. */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra(Extras.VERSION)) {
            setIntent(intent)
            recreate()
        }
    }

    /** 09 Switch league: the competitions you play in or follow; picking the cup or the league shows its Home. */
    private fun switchLeague(current: HomeVersion) {
        val sheet = SheetSwitchLeagueBinding.inflate(layoutInflater)
        sheet.head.sheetTitle.setText(R.string.league_switch_title)
        val dialog = showSheet(sheet.root)
        val mar = SampleData.teamName("MAR")
        val options = listOf(
            Triple(SampleData.LEAGUE.name, getString(R.string.league_you_play_for, getString(R.string.league_kind_league), mar), HomeVersion.LEAGUE),
            Triple(SampleData.CUP.name, getString(R.string.league_you_play_for, getString(R.string.league_kind_knockout), mar), HomeVersion.TOURNAMENT),
            Triple(OTHER_LEAGUE, getString(R.string.league_following, getString(R.string.league_kind_league)), null),
        )
        sheet.findMore.setOnClickListener {
            dialog.dismiss()
            go(SearchActivity::class)
        }
        val chosen = if (current == HomeVersion.TOURNAMENT) 1 else 0
        val radios = mutableListOf<RadioButton>()
        options.forEachIndexed { i, (name, meta, target) ->
            val row = ItemSheetOptionBinding.inflate(layoutInflater, sheet.options, true)
            row.name.text = name
            row.meta.text = meta
            row.radio.isChecked = i == chosen
            radios += row.radio
            val pick = View.OnClickListener {
                radios.forEachIndexed { j, radio -> radio.isChecked = j == i }
                if (target != null && target != current) {
                    dialog.dismiss()
                    go(HomeActivity::class, target)
                }
            }
            row.root.setOnClickListener(pick)
            row.radio.setOnClickListener(pick)
        }
    }

    private fun openResult(@Suppress("UNUSED_PARAMETER") row: MatchRow) = go(MatchActivity::class, MatchVersion.FULL_TIME)

    // The versions.

    private fun league() {
        leagueRow(SampleData.LEAGUE.name)
        b.notice.isVisible = true
        b.noticeText.text = getString(R.string.home_notice, SampleData.NEXT_MATCHDAY)
        nextMatch()
        table()
        results(2)
    }

    private fun tournament() {
        leagueRow(SampleData.CUP.name)
        hero(
            label = getString(R.string.home_next_match), pill = getString(R.string.home_final),
            away = getString(R.string.home_tbd_code), awayName = getString(R.string.home_either, "SAD", "RAW"),
            meta = listOf(FINAL_DATE, "7:00 pm", SampleData.LEAGUE.venue, "Court 2"),
        )
        nextButtons()
        section(b.semisSection, b.semisHead.title, R.string.home_semis)
        b.semiOne.bind(Tie("MAR", "CAP", 2, 2, note = getString(R.string.home_pens_note, "MAR", 4, 3), winner = "MAR"))
        b.semiTwo.bind(Tie("SAD", "RAW", note = keepTogether("Sun 23 Nov, 6:00 pm")))
        section(b.quartersSection, b.quartersHead.title, R.string.home_quarters)
        b.quarters.showRows(ResultRowAdapter(SampleData.CUP_QUARTERS) { go(StandingsActivity::class, StandingsVersion.BRACKET) })
    }

    private fun live() {
        leagueRow(SampleData.LEAGUE.name)
        hero(
            label = getString(R.string.home_live_now), live = getString(R.string.home_live, "32'"),
            mid = "2–1", midIsScore = true,
            meta = listOf(getString(R.string.home_matchday, SampleData.NEXT_MATCHDAY), SampleData.LEAGUE.venue, "Court 2"),
        )
        b.primary.setText(R.string.home_follow_live)
        b.secondary.setText(R.string.home_line_ups)
        b.secondary.icon = null
        section(b.eventsSection, b.eventsHead.title, R.string.home_in_this_match)
        val raw = SampleData.MAR_RAW
        b.events.showRows(EventAdapter(listOf(raw[5], raw[4], raw[3], raw[1], raw[0])), divided = false)
        section(b.earlierSection, b.earlierHead.title, R.string.home_earlier_today)
        b.earlier.showRows(ResultRowAdapter(SampleData.EARLIER_TODAY, ::openResult))
    }

    private fun nothingFollowed() {
        b.top.badge.isVisible = false
        b.league.root.isVisible = false
        b.hero.isVisible = false
        b.welcome.isVisible = true
        b.welcomeTitle.text = getString(R.string.home_welcome, SampleData.ME.name.substringBefore(' '))
        b.content.updatePadding(top = dp(24))
        section(b.popularSection, b.popularHead.title, getString(R.string.home_popular, SampleData.ME.city))
        val follow = getString(R.string.home_follow)
        b.popular.showRows(ActionRowAdapter(SampleData.SUGGESTIONS.map { (name, about) -> ActionRow(R.drawable.ic_emoji_events, name, about, follow) }))
    }

    private fun offline() {
        b.offline.root.isVisible = true
        b.offline.root.opensSyncStatus()
        (b.league.root.layoutParams as? android.view.ViewGroup.MarginLayoutParams)?.topMargin = dp(8)
        leagueRow(SampleData.LEAGUE.name)
        b.league.status.status(StatusKind.OFFLINE, getString(R.string.home_offline))
        nextMatch()
        b.saved.isVisible = true
        b.saved.text = getString(R.string.home_saved_on, keepTogether("Fri 14 Nov, 9:12 pm"))
        table()
        results(1)
    }

    private fun organizer() {
        leagueRow(SampleData.LEAGUE.name)
        val next = SampleData.MATCHDAY5.first { it.home == "MAR" }
        hero(
            label = keepTogether(getString(R.string.home_today, next.home, next.away, next.time.orEmpty())),
            pill = getString(R.string.home_you_organize),
            meta = listOf(getString(R.string.home_next_to_record, SampleData.LEAGUE.venue), next.venue.orEmpty()),
        )
        b.primary.setText(R.string.home_record_match)
        b.primary.setIconResource(R.drawable.ic_sports_soccer)
        b.secondary.setText(R.string.home_dashboard)
        b.secondary.icon = null
        section(b.attentionSection, b.attentionHead.title, R.string.home_attention)
        val requests = listOf("SAD", "PIN", "KOH").joinToString(", ") { SampleData.teamName(it) }
        val lineUps = ActionRow(R.drawable.ic_checklist, getString(R.string.home_lineups_not_set), keepTogether("${next.home} vs ${next.away}, ${next.time}"))
        b.attention.showRows(
            ActionRowAdapter(
                listOf(
                    ActionRow(R.drawable.ic_group_add, getString(R.string.home_team_requests, 3), "${SampleData.WINTER.name}: $requests", getString(R.string.home_review)),
                    lineUps,
                ),
                onClick = { row -> if (row == lineUps) go(LineupsActivity::class) else go(TeamRequestsActivity::class) },
                onAction = { go(TeamRequestsActivity::class) },
            ),
        )
        section(b.recordedSection, b.recordedHead.title, R.string.home_recorded_today)
        b.recorded.showRows(MatchRowAdapter(SampleData.EARLIER_TODAY.reversed().map { it.copy(state = MatchState.RESULT) }, ::openResult))
    }

    // Parts the versions share.

    private fun leagueRow(name: String) {
        b.league.leagueName.text = name
        b.league.status.status(StatusKind.SYNCED_ON_BLUE, getString(R.string.home_synced))
    }

    /** The next match, matchday 5's MAR vs RAW (League and Offline). */
    private fun nextMatch() {
        val next = SampleData.MATCHDAY5.first { it.home == "MAR" }
        hero(
            label = getString(R.string.home_next_match), pill = getString(R.string.home_matchday, SampleData.NEXT_MATCHDAY),
            meta = listOf(SampleData.MATCHDAY5_DATE, next.time.orEmpty(), SampleData.LEAGUE.venue, next.venue.orEmpty()),
        )
        nextButtons()
    }

    private fun nextButtons() {
        b.primary.setText(R.string.home_match_details)
        b.secondary.setText(R.string.home_remind_me)
        b.secondary.setIconResource(R.drawable.ic_notifications)
    }

    private fun hero(
        label: String,
        meta: List<String>,
        pill: String? = null,
        live: String? = null,
        mid: String = getString(R.string.home_vs),
        midIsScore: Boolean = false,
        home: String = "MAR",
        away: String = "RAW",
        awayName: String = SampleData.teamName(away),
    ) {
        b.heroLabel.text = label
        b.pill.isVisible = pill != null
        b.pill.text = pill
        b.livePill.isVisible = live != null
        if (live != null) b.livePill.status(StatusKind.LIVE, live)
        b.homeCode.text = home
        b.homeName.text = SampleData.teamName(home)
        b.awayCode.text = away
        b.awayName.text = awayName
        b.mid.isVisible = !midIsScore
        b.score.isVisible = midIsScore
        if (midIsScore) b.score.text = mid else b.mid.text = mid
        b.meta.text = keepTogether(meta.joinToString(", "))
    }

    private fun table() {
        section(b.tableSection, b.tableHead.title, R.string.home_table)
        b.table.showRows(
            TableAdapter(SampleData.TABLE.take(4), short = true, highlight = "MAR", playoffAfter = 4, onClick = { go(StandingsActivity::class, StandingsVersion.TABLE) }),
        )
    }

    private fun results(count: Int) {
        section(b.resultsSection, b.resultsHead.title, R.string.home_latest_results)
        b.results.showRows(ResultRowAdapter(SampleData.MATCHDAY4.take(count), ::openResult))
    }

    private fun section(section: View, title: android.widget.TextView, text: Int) = section(section, title, getString(text))

    private fun section(section: View, title: android.widget.TextView, text: String) {
        section.isVisible = true
        title.text = text
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).roundToInt()

    companion object {
        /** Extras.SHEET value that opens 09 Switch league over Home. */
        const val SHEET_SWITCH_LEAGUE = "switch_league"

        /** The Capital Futsal Cup final. */
        private const val FINAL_DATE = "Sat 29 Nov"

        /** A league the player follows but doesn't play in. */
        private const val OTHER_LEAGUE = "Lahore Futsal League"
    }
}
