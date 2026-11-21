package com.leaguex.app.kit.rows

import android.graphics.Color
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.leaguex.app.R
import com.leaguex.app.databinding.ItemNoteRowBinding
import com.leaguex.app.databinding.ItemSyncBinding
import com.leaguex.app.kit.StatusKind
import com.leaguex.app.kit.keepTogether
import com.leaguex.app.kit.status

/** A notification or announcement (ui.js noteRow). [unread] ones are tinted and carry a blue dot. */
data class NoteRow(
    @param:DrawableRes val icon: Int,
    val title: String,
    val body: String? = null,
    val time: String,
    val unread: Boolean = false,
)

class NoteRowAdapter(
    private val items: List<NoteRow>,
    private val onClick: ((NoteRow) -> Unit)? = null,
) : RecyclerView.Adapter<BindingHolder<ItemNoteRowBinding>>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BindingHolder(parent.inflateRow(ItemNoteRowBinding::inflate))

    override fun onBindViewHolder(holder: BindingHolder<ItemNoteRowBinding>, position: Int) {
        val n = items[position]
        val b = holder.binding
        b.icon.setImageResource(n.icon)
        b.headline.text = keepTogether(n.title)
        b.body.isVisible = n.body != null
        b.body.text = n.body?.let(::keepTogether)
        b.time.text = keepTogether(n.time)
        b.unreadDot.isVisible = n.unread
        b.root.setBackgroundColor(if (n.unread) ContextCompat.getColor(b.root.context, R.color.row_unread) else Color.TRANSPARENT)
        clickable(b.root, n, onClick)
    }
}

/**
 * Something saved on the phone and its upload (across.js syncItem): a [status] chip, or for a failed upload
 * ([failed]) the red chip under the meta and a retry button.
 */
data class SyncItem(
    @param:DrawableRes val icon: Int,
    val title: String,
    val meta: String,
    val status: StatusKind? = null,
    val statusText: String? = null,
    val failed: Boolean = false,
)

class SyncItemAdapter(
    private val items: List<SyncItem>,
    private val onRetry: ((SyncItem) -> Unit)? = null,
) : RecyclerView.Adapter<BindingHolder<ItemSyncBinding>>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BindingHolder(parent.inflateRow(ItemSyncBinding::inflate))

    override fun onBindViewHolder(holder: BindingHolder<ItemSyncBinding>, position: Int) {
        val s = items[position]
        val b = holder.binding
        b.icon.setImageResource(s.icon)
        b.title.text = s.title
        b.meta.text = keepTogether(s.meta)
        b.failed.isVisible = s.failed
        b.retry.isVisible = s.failed
        b.retry.setOnClickListener { onRetry?.invoke(s) }
        b.status.isVisible = !s.failed && s.status != null
        if (!s.failed && s.status != null) b.status.status(s.status, s.statusText.orEmpty())
        b.root.isClickable = false
    }
}
