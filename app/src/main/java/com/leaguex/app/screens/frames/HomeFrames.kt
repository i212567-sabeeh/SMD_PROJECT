package com.leaguex.app.screens.frames

import com.leaguex.app.home.HomeActivity
import com.leaguex.app.home.HomeVersion
import com.leaguex.app.screen
import com.leaguex.app.screens.Frame

/** Frames of the Figma section "Home (8)" (slug 02-home), in the Figma's order. */
val homeFrames: List<Frame> = listOf(
    Frame("s08-home-league", "08 Home / League") { it.screen(HomeActivity::class, HomeVersion.LEAGUE) },
    Frame("s08-home-tournament", "08 Home / Tournament") { it.screen(HomeActivity::class, HomeVersion.TOURNAMENT) },
    Frame("s08-home-live-match", "08 Home / Live match") { it.screen(HomeActivity::class, HomeVersion.LIVE) },
    Frame("s08-home-nothing-followed", "08 Home / Nothing followed") { it.screen(HomeActivity::class, HomeVersion.NOTHING) },
    Frame("s08-home-offline", "08 Home / Offline") { it.screen(HomeActivity::class, HomeVersion.OFFLINE) },
    Frame("s08-home-organizer", "08 Home / Organizer") { it.screen(HomeActivity::class, HomeVersion.ORGANIZER) },
)
