package com.leaguex.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import kotlin.reflect.KClass

/** Intent extras every screen understands. */
object Extras {
    /** Name of the screen's version enum constant, for example "LIVE". */
    const val VERSION = "version"

    /** Name of the bottom sheet to open over the screen, as the Figma draws it. */
    const val SHEET = "sheet"

    /** True when the screen opens with its menu showing. */
    const val MENU = "menu"

    /** Figma frame id the screen list opens straight away, for example "s08-home-league". */
    const val OPEN = "open"

    /**
     * An Intent to open once a flow has ended: the flow's first screen is brought back with this, which closes every
     * step above it, and then it opens the Intent and closes too. See LeagueXActivity.endFlow().
     */
    const val THEN = "then"
}

/** The Intent that shows [target], optionally in a version, with a sheet or with its menu open. */
fun Context.screen(
    target: KClass<out Activity>,
    version: Enum<*>? = null,
    sheet: String? = null,
    menu: Boolean = false,
): Intent = Intent(this, target.java).apply {
    if (version != null) putExtra(Extras.VERSION, version.name)
    if (sheet != null) putExtra(Extras.SHEET, sheet)
    if (menu) putExtra(Extras.MENU, true)
}
