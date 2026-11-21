package com.leaguex.app.screens.frames

import com.leaguex.app.across.LockScreenActivity
import com.leaguex.app.across.NotificationSettingsActivity
import com.leaguex.app.across.SettingsActivity
import com.leaguex.app.across.SyncStatusActivity
import com.leaguex.app.across.SyncStatusVersion
import com.leaguex.app.league.LeagueActivity
import com.leaguex.app.league.LeagueVersion
import com.leaguex.app.league.MatchesActivity
import com.leaguex.app.league.MatchesVersion
import com.leaguex.app.news.SearchActivity
import com.leaguex.app.news.SearchVersion
import com.leaguex.app.news.TeamActivity
import com.leaguex.app.news.TeamVersion
import com.leaguex.app.players.EditProfileActivity
import com.leaguex.app.players.EditProfileVersion
import com.leaguex.app.screen
import com.leaguex.app.screens.Frame

/**
 * Frames of the Figma section "Across the app (52–57)" (slug 09-across), in the Figma's order. 53's and 57's states
 * live on the screens they belong to, built with those screens.
 */
val acrossFrames: List<Frame> = listOf(
    Frame("s52-sync-status", "52 Sync status") { it.screen(SyncStatusActivity::class, SyncStatusVersion.WAITING) },
    Frame("s53-photo-upload-pick", "53 Photo upload / Pick") {
        it.screen(EditProfileActivity::class, EditProfileVersion.DEFAULT, EditProfileActivity.SHEET_PHOTO)
    },
    Frame("s53-photo-upload-uploading", "53 Photo upload / Uploading") { it.screen(EditProfileActivity::class, EditProfileVersion.UPLOADING) },
    Frame("s54-push-notifications", "54 Push notifications") { it.screen(LockScreenActivity::class) },
    Frame("s55-settings", "55 Settings") { it.screen(SettingsActivity::class) },
    Frame("s56-notification-settings", "56 Notification settings") { it.screen(NotificationSettingsActivity::class) },
    Frame("s57-empty-no-search-results", "57 Empty / No search results") { it.screen(SearchActivity::class, SearchVersion.NO_RESULTS) },
    Frame("s57-empty-no-matches-yet", "57 Empty / No matches yet") { it.screen(MatchesActivity::class, MatchesVersion.NO_MATCHES) },
    Frame("s57-loading-images", "57 Loading / Images") { it.screen(TeamActivity::class, TeamVersion.LOADING) },
    Frame("s57-error-no-connection", "57 Error / No connection") { it.screen(LeagueActivity::class, LeagueVersion.NO_CONNECTION) },
    Frame("s57-error-upload-failed", "57 Error / Upload failed") { it.screen(SyncStatusActivity::class, SyncStatusVersion.UPLOAD_FAILED) },
)
