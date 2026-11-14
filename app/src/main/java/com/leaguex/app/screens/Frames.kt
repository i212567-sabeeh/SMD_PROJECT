package com.leaguex.app.screens

import com.leaguex.app.screens.frames.acrossFrames
import com.leaguex.app.screens.frames.captainsFrames
import com.leaguex.app.screens.frames.homeFrames
import com.leaguex.app.screens.frames.leagueFrames
import com.leaguex.app.screens.frames.matchdayFrames
import com.leaguex.app.screens.frames.newsFrames
import com.leaguex.app.screens.frames.organizerFrames
import com.leaguex.app.screens.frames.playersFrames
import com.leaguex.app.screens.frames.startFrames

/** Every Figma frame and the Intent that shows it, in the Figma's 9 sections. Slugs and titles match the manifest. */
object Frames {
    val sections: List<FrameSection> = listOf(
        FrameSection("01-getting-started", "Getting started (1–7)", startFrames),
        FrameSection("02-home", "Home (8)", homeFrames),
        FrameSection("03-league", "League, matches and standings (9, 12–17)", leagueFrames),
        FrameSection("04-news", "Teams, players and news (10, 11, 18–23)", newsFrames),
        FrameSection("05-players", "Players (24–27)", playersFrames),
        FrameSection("06-captains", "Captains (28–31)", captainsFrames),
        FrameSection("07-organizer-setup", "Organizers: setting up (32–44)", organizerFrames),
        FrameSection("08-match-day", "Organizers: match day (45–51)", matchdayFrames),
        FrameSection("09-across", "Across the app (52–57)", acrossFrames),
    )

    val all: List<Frame> = sections.flatMap { it.frames }

    fun byId(id: String): Frame? = all.firstOrNull { it.id == id }
}
