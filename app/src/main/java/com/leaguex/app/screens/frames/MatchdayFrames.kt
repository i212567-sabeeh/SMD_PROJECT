package com.leaguex.app.screens.frames

import com.leaguex.app.matchday.ConfirmFullTimeActivity
import com.leaguex.app.matchday.LineupsActivity
import com.leaguex.app.matchday.PenaltiesActivity
import com.leaguex.app.matchday.RecordMatchActivity
import com.leaguex.app.matchday.RecordMatchVersion
import com.leaguex.app.screen
import com.leaguex.app.screens.Frame

/** Frames of the Figma section "Organizers: match day (45–51)" (slug 08-match-day), in the Figma's order. */
val matchdayFrames: List<Frame> = listOf(
    Frame("s45-line-ups", "45 Line-ups") { it.screen(LineupsActivity::class) },
    Frame("s46-record-first-half", "46 Record match / First half") { it.screen(RecordMatchActivity::class, RecordMatchVersion.FIRST_HALF) },
    Frame("s46-record-half-time", "46 Record match / Half time") { it.screen(RecordMatchActivity::class, RecordMatchVersion.HALF_TIME) },
    Frame("s46-record-extra-time", "46 Record match / Extra time") { it.screen(RecordMatchActivity::class, RecordMatchVersion.EXTRA_TIME) },
    Frame("s46-record-offline", "46 Record match / Offline") { it.screen(RecordMatchActivity::class, RecordMatchVersion.OFFLINE) },
    Frame("s47-add-goal", "47 Add goal") {
        it.screen(RecordMatchActivity::class, RecordMatchVersion.OFFLINE, RecordMatchActivity.SHEET_ADD_GOAL)
    },
    Frame("s48-add-card", "48 Add card") {
        it.screen(RecordMatchActivity::class, RecordMatchVersion.OFFLINE, RecordMatchActivity.SHEET_ADD_CARD)
    },
    Frame("s49-add-substitution", "49 Add substitution") {
        it.screen(RecordMatchActivity::class, RecordMatchVersion.OFFLINE, RecordMatchActivity.SHEET_ADD_SUB)
    },
    Frame("s50-penalty-shootout", "50 Penalty shootout") { it.screen(PenaltiesActivity::class) },
    Frame("s51-confirm-full-time", "51 Confirm full time") { it.screen(ConfirmFullTimeActivity::class) },
)
