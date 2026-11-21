package com.leaguex.app.kit

import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.leaguex.app.R
import com.leaguex.app.databinding.IncludeNavRowBinding
import com.leaguex.app.databinding.IncludePairingBinding
import com.leaguex.app.databinding.IncludeSettingLineBinding
import com.leaguex.app.databinding.IncludeSummaryCardBinding
import com.leaguex.app.databinding.IncludeTileBinding

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

/** A row that opens a setting's page: [icon], [label] and its current [value], and a chevron. */
fun IncludeNavRowBinding.bind(@DrawableRes icon: Int, @StringRes label: Int, value: CharSequence, onClick: (() -> Unit)? = null) {
    this.icon.setImageResource(icon)
    this.label.setText(label)
    this.value.text = value
    if (onClick != null) root.setOnClickListener { onClick() } else root.isClickable = false
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
            TextView(root.context).apply {
                TextViewCompat.setTextAppearance(this, if (i == 0) R.style.TextAppearance_LeagueX_Title_Small else R.style.TextAppearance_LeagueX_Body_Medium)
                if (i > 0) setTextColor(ContextCompat.getColor(context, R.color.muted))
                text = keepTogether(line.toString())
            },
        )
    }
}
