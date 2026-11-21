package com.leaguex.app.kit

import android.view.Gravity
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.core.widget.TextViewCompat
import com.leaguex.app.R

/** The status chips (ui.js status(), kit.css .status.*), each drawn by its Widget.LeagueX.Status.<Kind> style. */
enum class StatusKind(@get:StyleRes val style: Int) {
    SYNCED(R.style.Widget_LeagueX_Status_Synced),
    SYNCED_ON_BLUE(R.style.Widget_LeagueX_Status_SyncedOnBlue),
    WAITING(R.style.Widget_LeagueX_Status_Waiting),
    OFFLINE(R.style.Widget_LeagueX_Status_Offline),
    FAILED(R.style.Widget_LeagueX_Status_Failed),
    LIVE(R.style.Widget_LeagueX_Status_Live),
    UPDATED(R.style.Widget_LeagueX_Status_Updated),
    ON_BLUE(R.style.Widget_LeagueX_Status_OnBlue),
    NEUTRAL(R.style.Widget_LeagueX_Status_Neutral),
}

/** The attributes a status style sets, sorted as obtainStyledAttributes needs them. */
private val STATUS_ATTRS = intArrayOf(
    android.R.attr.textAppearance,
    android.R.attr.background,
    android.R.attr.backgroundTint,
    android.R.attr.textColor,
    android.R.attr.drawableStart,
    android.R.attr.drawableTint,
    android.R.attr.drawablePadding,
    android.R.attr.minHeight,
    android.R.attr.paddingStart,
    android.R.attr.paddingEnd,
    android.R.attr.gravity,
).apply { sort() }

private fun at(attr: Int) = STATUS_ATTRS.indexOf(attr)

/**
 * Shows [text] as a [kind] status chip: the look a TextView styled Widget.LeagueX.Status.<Kind> has, read from
 * that style, so a chip whose kind changes at run time matches the ones written in layouts.
 */
fun TextView.status(kind: StatusKind, text: CharSequence) {
    val a = context.obtainStyledAttributes(kind.style, STATUS_ATTRS)
    try {
        a.getResourceId(at(android.R.attr.textAppearance), 0).takeIf { it != 0 }?.let { TextViewCompat.setTextAppearance(this, it) }
        background = a.getDrawable(at(android.R.attr.background))
        backgroundTintList = a.getColorStateList(at(android.R.attr.backgroundTint))
        a.getColorStateList(at(android.R.attr.textColor))?.let(::setTextColor)
        setCompoundDrawablesRelativeWithIntrinsicBounds(a.getDrawable(at(android.R.attr.drawableStart)), null, null, null)
        TextViewCompat.setCompoundDrawableTintList(this, a.getColorStateList(at(android.R.attr.drawableTint)))
        compoundDrawablePadding = a.getDimensionPixelSize(at(android.R.attr.drawablePadding), 0)
        minHeight = a.getDimensionPixelSize(at(android.R.attr.minHeight), 0)
        setPaddingRelative(
            a.getDimensionPixelSize(at(android.R.attr.paddingStart), 0), 0,
            a.getDimensionPixelSize(at(android.R.attr.paddingEnd), 0), 0,
        )
        gravity = a.getInt(at(android.R.attr.gravity), Gravity.CENTER_VERTICAL)
    } finally {
        a.recycle()
    }
    maxLines = 1
    includeFontPadding = false
    this.text = text
}
