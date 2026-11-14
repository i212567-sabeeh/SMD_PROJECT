package com.leaguex.app.screens

import android.content.Context
import android.content.Intent

/** One Figma frame: its id and name exactly as in the Figma, and the Intent that shows it. */
data class Frame(val id: String, val name: String, val open: (Context) -> Intent)

/** One Figma section (board), with its frames in the Figma's order. */
data class FrameSection(val slug: String, val title: String, val frames: List<Frame>)
