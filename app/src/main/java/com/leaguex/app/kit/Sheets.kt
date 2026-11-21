package com.leaguex.app.kit

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.view.WindowManager
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.leaguex.app.R
import com.leaguex.app.databinding.ItemSheetActionBinding

/**
 * Opens [content] as a bottom sheet over this screen (ui.js sheet): already expanded, paper with 28dp top corners,
 * over a scrim. [content] starts with include_sheet_head, whose close button dismisses the sheet, and puts the rest
 * in a Widget.LeagueX.SheetBody. The sheet pads itself by the navigation bar, so its layout needs no bottom padding.
 *
 * The sheet's window keeps the status-bar icons of the screen under it, so a sheet over a blue top keeps white icons.
 */
fun Activity.showSheet(content: View): BottomSheetDialog {
    val sheet = BottomSheetDialog(this, R.style.ThemeOverlay_LeagueX_BottomSheetDialog)
    sheet.setContentView(content)
    content.findViewById<View>(R.id.sheetClose)?.setOnClickListener { sheet.dismiss() }
    sheet.behavior.skipCollapsed = true
    sheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    sheet.window?.let { window ->
        // The scrim (kit.css --color-scrim) is the dialog's dim, at the scrim colour's opacity.
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.setDimAmount(Color.alpha(ContextCompat.getColor(this, R.color.scrim)) / 255f)
        val under = WindowCompat.getInsetsController(this.window, this.window.decorView)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = under.isAppearanceLightStatusBars
            isAppearanceLightNavigationBars = true
        }
    }
    sheet.show()
    return sheet
}

/** One action in a sheet (ui.js sheetRow): a blue [icon] and its [label], or both in red when [danger]. */
fun ItemSheetActionBinding.bind(@DrawableRes icon: Int, label: CharSequence, danger: Boolean = false) {
    val context = root.context
    root.text = label
    root.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, 0, 0, 0)
    TextViewCompat.setCompoundDrawableTintList(
        root,
        ColorStateList.valueOf(ContextCompat.getColor(context, if (danger) R.color.error else R.color.blue)),
    )
    root.setTextColor(ContextCompat.getColor(context, if (danger) R.color.error else R.color.ink))
}
