package com.leaguex.app.screens.frames

import com.leaguex.app.home.HomeActivity
import com.leaguex.app.home.HomeVersion
import com.leaguex.app.league.LeagueActivity
import com.leaguex.app.league.MatchActivity
import com.leaguex.app.league.MatchVersion
import com.leaguex.app.league.MatchesActivity
import com.leaguex.app.league.StandingsActivity
import com.leaguex.app.league.StandingsVersion
import com.leaguex.app.screen
import com.leaguex.app.screens.Frame

/** Frames of the Figma section "League, matches and standings (9, 12–17)" (slug 03-league), in the Figma's order. */
val leagueFrames: List<Frame> = listOf(
    Frame("s09-switch-league", "09 Switch league") {
        it.screen(HomeActivity::class, HomeVersion.LEAGUE, sheet = HomeActivity.SHEET_SWITCH_LEAGUE)
    },
    Frame("s12-league-page", "12 League page") { it.screen(LeagueActivity::class) },
    Frame("s13-matches", "13 Matches") { it.screen(MatchesActivity::class) },
    Frame("s14-match-before-kick-off", "14 Match page / Before kick-off") { it.screen(MatchActivity::class, MatchVersion.BEFORE) },
    Frame("s14-match-live", "14 Match page / Live") { it.screen(MatchActivity::class, MatchVersion.LIVE) },
    Frame("s14-match-full-time", "14 Match page / Full time") { it.screen(MatchActivity::class, MatchVersion.FULL_TIME) },
    Frame("s14-match-won-on-penalties", "14 Match page / Won on penalties") { it.screen(MatchActivity::class, MatchVersion.PENALTIES) },
    Frame("s14-match-organizer", "14 Match page / Organizer") { it.screen(MatchActivity::class, MatchVersion.ORGANIZER) },
    Frame("s15-table", "15 Table") { it.screen(StandingsActivity::class, StandingsVersion.TABLE) },
    Frame("s16-bracket", "16 Bracket") { it.screen(StandingsActivity::class, StandingsVersion.BRACKET) },
    Frame("s17-stats", "17 Stats") { it.screen(StandingsActivity::class, StandingsVersion.STATS) },
)
