package com.leaguex.app.kit

import android.content.res.ColorStateList
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import com.google.android.material.textview.MaterialTextView
import com.leaguex.app.R
import com.leaguex.app.databinding.IncludeNavRowBinding
import com.leaguex.app.databinding.IncludePairingBinding
import com.leaguex.app.databinding.IncludePushCardBinding
import com.leaguex.app.databinding.IncludeSettingLineBinding
import com.leaguex.app.databinding.IncludeSummaryCardBinding
import com.leaguex.app.databinding.IncludeTileBinding
import com.leaguex.app.databinding.IncludeToggleRowBinding

/** A setting with a switch: [title], an optional [desc] under it, and the switch set to [on]. */
fun IncludeSettingLineBinding.bind(@StringRes title: Int, @StringRes desc: Int?, on: Boolean) {
    this.title.setText(title)
    if (desc != null) this.desc.setText(desc) else this.desc.visibility = android.view.View.GONE
    toggle.isChecked = on
    toggle.contentDescription = root.context.getString(title)
}

/** A shortcut tile: [icon] in its tint circle over [label]; tapping it runs [onClick]. */
fun IncludeTileBinding.bind(@DrawableRes icon: Int, @StringRes label: Int, onClick: () -> Unit) {
    this.icon.setImageResource(icon)
    this.label.setText(label)
    root.setOnClickListener { onClick() }
}

/**
 * A row that opens a setting's page: [icon], [label] and its current [value] (none when blank), and a chevron. A
 * [danger] row (Log out) is red and has no chevron.
 */
fun IncludeNavRowBinding.bind(
    @DrawableRes icon: Int,
    @StringRes label: Int,
    value: CharSequence,
    danger: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    this.icon.setImageResource(icon)
    this.label.setText(label)
    this.value.text = value
    this.value.isVisible = value.isNotBlank()
    chevron.isVisible = !danger
    if (danger) {
        val red = ContextCompat.getColor(root.context, R.color.error)
        this.icon.imageTintList = ColorStateList.valueOf(red)
        this.label.setTextColor(red)
    }
    if (onClick != null) root.setOnClickListener { onClick() } else root.isClickable = false
}

/** A setting with a switch in a list (ui.js settingRow, trailing toggle): a muted [icon], its [label], and the switch. */
fun IncludeToggleRowBinding.bind(@DrawableRes icon: Int, @StringRes label: Int, on: Boolean) {
    this.icon.setImageResource(icon)
    this.label.setText(label)
    toggle.isChecked = on
    toggle.contentDescription = root.context.getString(label)
    root.setOnClickListener { toggle.toggle() }
}

/** A draw pairing: [label] ("QF1"), then teams [a] and [b] either side of "vs". */
fun IncludePairingBinding.bind(label: CharSequence, a: String, b: String) {
    this.label.text = label
    crestA.crest(a)
    codeA.text = a
    codeB.text = b
    crestB.crest(b)
}

/** A review card: the step's [title], its answers ([lines], the first in bold), and Edit, which runs [onEdit]. */
fun IncludeSummaryCardBinding.bind(title: CharSequence, lines: List<CharSequence>, onEdit: () -> Unit) {
    this.title.text = title
    edit.setOnClickListener { onEdit() }
    this.lines.removeAllViews()
    lines.forEachIndexed { i, line ->
        this.lines.addView(
            MaterialTextView(root.context).apply {
                TextViewCompat.setTextAppearance(this, if (i == 0) R.style.TextAppearance_LeagueX_Title_Small else R.style.TextAppearance_LeagueX_Body_Medium)
                if (i > 0) setTextColor(ContextCompat.getColor(context, R.color.muted))
                text = keepTogether(line.toString())
            },
        )
    }
}

/** A lock-screen notification (ui.js push): "LeagueX, [time]" over its [title] and [body]. */
fun IncludePushCardBinding.bind(title: CharSequence, body: CharSequence, time: String) {
    head.text = keepTogether(root.context.getString(R.string.kit_push_head, time))
    this.title.text = title
    this.body.text = keepTogether(body.toString())
}
