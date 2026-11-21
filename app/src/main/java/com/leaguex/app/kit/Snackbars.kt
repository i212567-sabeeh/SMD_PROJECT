package com.leaguex.app.kit

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.leaguex.app.R

/**
 * The Figma's dark snackbar (ui.js snackbar) saying what was saved on this phone, held until the phone is back
 * online. It sits above [anchor] when there is one, such as a footer. The theme's snackbarStyle and
 * snackbarTextViewStyle draw it as kit.css .snackbar.
 */
fun View.showSavedOffline(anchor: View? = null): Snackbar =
    Snackbar.make(this, R.string.edit_saved_offline, Snackbar.LENGTH_INDEFINITE)
        .setAnchorView(anchor)
        .setTextMaxLines(3)
        .also { it.show() }
