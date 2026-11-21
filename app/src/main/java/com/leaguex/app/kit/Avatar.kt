package com.leaguex.app.kit

import android.widget.TextView

/**
 * Makes this TextView [name]'s avatar: the first letters of their first two names, in capitals ("Usman Tariq"
 * shows "UT"), as ui.js initials() does. Style the view Widget.LeagueX.Avatar.<Size>.
 */
fun TextView.avatar(name: String) {
    text = name.split(' ').filter { it.isNotEmpty() }.take(2).map { it.first() }.joinToString("").uppercase()
    contentDescription = name
}
