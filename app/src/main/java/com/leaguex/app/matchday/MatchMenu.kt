package com.leaguex.app.matchday

import android.app.Activity
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import com.leaguex.app.R
import com.leaguex.app.screen

/**
 * The more menu on 46 Record match and 50 Penalty shootout (the Figma draws the button, not the menu): the
 * line-ups and, when [penalties], the penalty shootout.
 */
internal fun Activity.matchMenu(anchor: View, penalties: Boolean) {
    val popup = PopupMenu(this, anchor, Gravity.END)
    popup.menuInflater.inflate(R.menu.menu_record_match, popup.menu)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) popup.setForceShowIcon(true)
    popup.menu.findItem(R.id.penalties)?.isVisible = penalties
    popup.setOnMenuItemClickListener { item ->
        when (item.itemId) {
            R.id.lineups -> startActivity(screen(LineupsActivity::class))
            R.id.penalties -> startActivity(screen(PenaltiesActivity::class))
        }
        true
    }
    popup.show()
}
