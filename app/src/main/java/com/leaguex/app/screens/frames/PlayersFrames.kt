package com.leaguex.app.screens.frames

import com.leaguex.app.players.EditProfileActivity
import com.leaguex.app.players.EditProfileVersion
import com.leaguex.app.players.JoinTeamActivity
import com.leaguex.app.players.ProfileActivity
import com.leaguex.app.players.ProfileVersion
import com.leaguex.app.players.TeamInviteActivity
import com.leaguex.app.screen
import com.leaguex.app.screens.Frame

/** Frames of the Figma section "Players (24–27)" (slug 05-players), in the Figma's order. */
val playersFrames: List<Frame> = listOf(
    Frame("s24-my-profile", "24 My profile") { it.screen(ProfileActivity::class, ProfileVersion.PLAYER) },
    Frame("s24-my-profile-organizer", "24 My profile / Organizer") { it.screen(ProfileActivity::class, ProfileVersion.ORGANIZER) },
    Frame("s25-edit-profile", "25 Edit profile") { it.screen(EditProfileActivity::class, EditProfileVersion.DEFAULT) },
    Frame("s25-edit-profile-saved-offline", "25 Edit profile / Saved offline") {
        it.screen(EditProfileActivity::class, EditProfileVersion.SAVED_OFFLINE)
    },
    Frame("s26-join-a-team", "26 Join a team") { it.screen(JoinTeamActivity::class) },
    Frame("s27-team-invite", "27 Team invite") { it.screen(TeamInviteActivity::class) },
)
