package com.leaguex.app.kit.rows

import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.leaguex.app.databinding.ItemActionRowBinding

/** A row that asks for something: [icon], [title] and [meta], then a tonal [action] button or, without one, a chevron. */
data class ActionRow(
    @param:DrawableRes val icon: Int,
    val title: String,
    val meta: String,
    val action: String? = null,
)

/** Action rows (08 Home's suggestions and organizer to-dos). [onClick] hears the row; [onAction] its button. */
class ActionRowAdapter(
    private val items: List<ActionRow>,
    private val onClick: ((ActionRow) -> Unit)? = null,
    private val onAction: ((ActionRow) -> Unit)? = null,
) : RecyclerView.Adapter<BindingHolder<ItemActionRowBinding>>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BindingHolder(parent.inflateRow(ItemActionRowBinding::inflate))

    override fun onBindViewHolder(holder: BindingHolder<ItemActionRowBinding>, position: Int) {
        val a = items[position]
        val b = holder.binding
        b.icon.setImageResource(a.icon)
        b.title.text = a.title
        b.meta.text = a.meta
        b.action.isVisible = a.action != null
        b.action.text = a.action
        b.action.setOnClickListener { onAction?.invoke(a) }
        b.chevron.isVisible = a.action == null
        clickable(b.root, a, onClick)
    }
}
