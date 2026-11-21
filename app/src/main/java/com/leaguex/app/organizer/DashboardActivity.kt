package com.leaguex.app.organizer

import android.os.Bundle
import androidx.core.view.isVisible
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityDashboardBinding
import com.leaguex.app.databinding.SheetEditMatchBinding
import com.leaguex.app.kit.bind
import com.leaguex.app.kit.crest
import com.leaguex.app.kit.keepTogether
import com.leaguex.app.kit.rows.ActionRow
import com.leaguex.app.kit.rows.ActionRowAdapter
import com.leaguex.app.kit.rows.showRows
import com.leaguex.app.kit.showSheet

/** 37 Organizer dashboard: today's match, team requests, and shortcuts. Edit matches opens 41 Edit match. */
class DashboardActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.content)
        b.top.title.setText(R.string.dashboard_title)
        backOn(b.top.back)
        b.top.action.isVisible = true
        b.top.action.setIconResource(R.drawable.ic_settings)
        b.top.action.contentDescription = getString(R.string.dashboard_settings)
        b.top.action.setOnClickListener { go(LeagueSettingsActivity::class) }

        val league = SampleData.LEAGUE
        b.leagueLine.text = getString(R.string.dashboard_league_line, league.name, SampleData.NEXT_MATCHDAY, MATCHDAYS)
        val today = SampleData.MATCHDAY5.first { it.home == "MAR" }
        b.homeCrest.crest(today.home)
        b.awayCrest.crest(today.away)
        b.match.text = "${today.home} vs ${today.away}"
        b.matchWhen.text = keepTogether("${today.time}, ${today.venue}")

        val requests = listOf("SAD", "PIN", "KOH").joinToString(", ") { SampleData.teamName(it) }
        b.requests.showRows(
            ActionRowAdapter(
                listOf(ActionRow(R.drawable.ic_group_add, getString(R.string.home_team_requests, 3), "${SampleData.WINTER.name}: $requests", getString(R.string.home_review))),
                onAction = { go(TeamRequestsActivity::class) },
            ),
        )
        b.shortcutsHead.title.setText(R.string.dashboard_shortcuts)
        b.fixtures.bind(R.drawable.ic_calendar_month, R.string.dashboard_fixtures) { go(FixturesActivity::class) }
        b.post.bind(R.drawable.ic_campaign, R.string.dashboard_post) { go(PostAnnouncementActivity::class) }
        b.editMatches.bind(R.drawable.ic_edit_calendar, R.string.dashboard_edit_matches) { editMatch() }
        b.settings.bind(R.drawable.ic_settings, R.string.dashboard_settings) { go(LeagueSettingsActivity::class) }

        if (sheetToOpen == SHEET_EDIT_MATCH) editMatch()
    }

    /** 41 Edit match: KHY vs KOH, moved to 5:30 pm. Saving tells both teams; nothing is sent anywhere yet. */
    private fun editMatch() {
        val sheet = SheetEditMatchBinding.inflate(layoutInflater)
        sheet.head.sheetTitle.setText(R.string.edit_match_title)
        val match = SampleData.MATCHDAY5.first { it.home == "KHY" }
        sheet.homeCrest.crest(match.home)
        sheet.awayCrest.crest(match.away)
        sheet.match.text = "${SampleData.teamName(match.home)} vs ${SampleData.teamName(match.away)}"
        sheet.date.setText(SampleData.MATCHDAY5_DATE)
        sheet.time.setText(match.time)
        sheet.court.setText(match.venue, false)
        sheet.notify.bind(R.string.edit_match_notify, R.string.edit_match_notify_body, on = true)
        val dialog = showSheet(sheet.root)
        sheet.save.setOnClickListener { dialog.dismiss() }
    }

    companion object {
        /** Extras.SHEET value that opens 41 Edit match over the dashboard. */
        const val SHEET_EDIT_MATCH = "edit_match"

        private const val MATCHDAYS = 14
    }
}
