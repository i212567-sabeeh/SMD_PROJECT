package com.leaguex.app.kit

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.leaguex.app.databinding.IncludeOptionCardBinding

/** An option card (kit.css .option-card): [icon] in its tint tile, [title] and [body]; tapping it runs [onClick]. */
fun IncludeOptionCardBinding.bind(@DrawableRes icon: Int, @StringRes title: Int, @StringRes body: Int, onClick: () -> Unit) {
    glyph.setImageResource(icon)
    this.title.setText(title)
    this.body.setText(body)
    root.setOnClickListener { onClick() }
}
