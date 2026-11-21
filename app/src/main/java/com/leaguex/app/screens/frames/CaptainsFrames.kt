package com.leaguex.app.screens.frames

import com.leaguex.app.captains.AddPlayersActivity
import com.leaguex.app.captains.EditTeamActivity
import com.leaguex.app.captains.ManageSquadActivity
import com.leaguex.app.captains.RegisterTeamActivity
import com.leaguex.app.captains.RegisterTeamVersion
import com.leaguex.app.screen
import com.leaguex.app.screens.Frame

/** Frames of the Figma section "Captains (28–31)" (slug 06-captains), in the Figma's order. */
val captainsFrames: List<Frame> = listOf(
    Frame("s28-register-a-team", "28 Register a team") { it.screen(RegisterTeamActivity::class, RegisterTeamVersion.DEFAULT) },
    Frame("s28-register-waiting", "28 Register a team / Waiting for approval") {
        it.screen(RegisterTeamActivity::class, RegisterTeamVersion.WAITING)
    },
    Frame("s29-manage-squad", "29 Manage squad") { it.screen(ManageSquadActivity::class, menu = true) },
    Frame("s30-add-players", "30 Add players") { it.screen(AddPlayersActivity::class) },
    Frame("s31-edit-team", "31 Edit team") { it.screen(EditTeamActivity::class) },
)
