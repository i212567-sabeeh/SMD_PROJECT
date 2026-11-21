package com.leaguex.app.screens.frames

import com.leaguex.app.news.AnnouncementActivity
import com.leaguex.app.news.AnnouncementsActivity
import com.leaguex.app.news.NotificationsActivity
import com.leaguex.app.news.PlayerActivity
import com.leaguex.app.news.SearchActivity
import com.leaguex.app.news.TeamActivity
import com.leaguex.app.news.TeamVersion
import com.leaguex.app.news.TeamsActivity
import com.leaguex.app.screen
import com.leaguex.app.screens.Frame

/** Frames of the Figma section "Teams, players and news (10, 11, 18–23)" (slug 04-news), in the Figma's order. */
val newsFrames: List<Frame> = listOf(
    Frame("s10-search", "10 Search") { it.screen(SearchActivity::class) },
    Frame("s11-filters", "11 Filters") { it.screen(SearchActivity::class, sheet = SearchActivity.SHEET_FILTERS) },
    Frame("s18-teams", "18 Teams") { it.screen(TeamsActivity::class) },
    Frame("s19-team-page", "19 Team page") { it.screen(TeamActivity::class, TeamVersion.DEFAULT) },
    Frame("s19-team-page-captain", "19 Team page / Captain") { it.screen(TeamActivity::class, TeamVersion.CAPTAIN) },
    Frame("s20-player-page", "20 Player page") { it.screen(PlayerActivity::class) },
    Frame("s21-announcements", "21 Announcements") { it.screen(AnnouncementsActivity::class) },
    Frame("s22-announcement-detail", "22 Announcement detail") { it.screen(AnnouncementActivity::class) },
    Frame("s23-notifications", "23 Notifications") { it.screen(NotificationsActivity::class) },
)
