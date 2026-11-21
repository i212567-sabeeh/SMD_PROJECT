package com.leaguex.app.screens.frames

import com.leaguex.app.organizer.CreateDetailsActivity
import com.leaguex.app.organizer.CreateKind
import com.leaguex.app.organizer.CreateReviewActivity
import com.leaguex.app.organizer.CreateRulesActivity
import com.leaguex.app.organizer.CreateTypeActivity
import com.leaguex.app.organizer.DashboardActivity
import com.leaguex.app.organizer.DrawActivity
import com.leaguex.app.organizer.FixturesActivity
import com.leaguex.app.organizer.LeagueSettingsActivity
import com.leaguex.app.organizer.PostAnnouncementActivity
import com.leaguex.app.organizer.PublishedActivity
import com.leaguex.app.organizer.StartPlayoffsActivity
import com.leaguex.app.organizer.TeamRequestsActivity
import com.leaguex.app.screen
import com.leaguex.app.screens.Frame

/** Frames of the Figma section "Organizers: setting up (32–44)" (slug 07-organizer-setup), in the Figma's order. */
val organizerFrames: List<Frame> = listOf(
    Frame("s32-create-type", "32 Create / Type") { it.screen(CreateTypeActivity::class) },
    Frame("s33-create-details", "33 Create / Details") { it.screen(CreateDetailsActivity::class, CreateKind.LEAGUE) },
    Frame("s34-create-rules-league", "34 Create / Rules, league") { it.screen(CreateRulesActivity::class, CreateKind.LEAGUE) },
    Frame("s34-create-rules-tournament", "34 Create / Rules, tournament") { it.screen(CreateRulesActivity::class, CreateKind.TOURNAMENT) },
    Frame("s35-create-review", "35 Create / Review") { it.screen(CreateReviewActivity::class, CreateKind.LEAGUE) },
    Frame("s36-published", "36 Published") { it.screen(PublishedActivity::class) },
    Frame("s37-organizer-dashboard", "37 Organizer dashboard") { it.screen(DashboardActivity::class) },
    Frame("s38-team-requests", "38 Team requests") { it.screen(TeamRequestsActivity::class) },
    Frame("s39-generate-fixtures", "39 Generate fixtures") { it.screen(FixturesActivity::class) },
    Frame("s40-draw", "40 Draw") { it.screen(DrawActivity::class) },
    Frame("s41-edit-match", "41 Edit match") { it.screen(DashboardActivity::class, sheet = DashboardActivity.SHEET_EDIT_MATCH) },
    Frame("s42-start-playoffs", "42 Start playoffs") { it.screen(StartPlayoffsActivity::class) },
    Frame("s43-post-announcement", "43 Post announcement") { it.screen(PostAnnouncementActivity::class) },
    Frame("s44-league-settings", "44 League settings") { it.screen(LeagueSettingsActivity::class) },
)
