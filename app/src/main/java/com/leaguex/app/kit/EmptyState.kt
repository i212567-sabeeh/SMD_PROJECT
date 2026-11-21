package com.leaguex.app.kit

import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import com.leaguex.app.databinding.IncludeEmptyStateBinding

/** An empty or error state (ui.js empty): [icon] in a tint circle, [title], [body] and an optional filled [button]. */
fun IncludeEmptyStateBinding.bind(@DrawableRes icon: Int, title: CharSequence, body: CharSequence, button: CharSequence? = null) {
    this.icon.setImageResource(icon)
    this.title.text = title
    this.body.text = body
    this.button.text = button
    this.button.isVisible = button != null
}
