package com.leaguex.app.kit.rows

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ItemPlayerRowBinding
import com.leaguex.app.databinding.ItemTeamRowBinding
import com.leaguex.app.kit.StatusKind
import com.leaguex.app.kit.avatar
import com.leaguex.app.kit.crest
import com.leaguex.app.kit.status

/** What a player row shows on its right (ui.js playerRow trailing). */
sealed interface Trail {
    data object Chevron : Trail
    data object None : Trail
    data class Stat(val value: String) : Trail
    data class Check(val on: Boolean) : Trail
    data object Invite : Trail
    data object Pending : Trail
    data object Call : Trail
    data object Remove : Trail

    /** 29 Manage squad: call and more options. */
    data object CallAndMore : Trail

    /** 17 Stats bookings: yellow cards, then red ones when there are any. */
    data class Cards(val yellow: Int, val red: Int = 0) : Trail
}

/** A player in a list: name, an optional meta line ("Forward, #11") and shirt [number], and what's on the right. */
data class PlayerRow(val name: String, val meta: String? = null, val trail: Trail = Trail.Chevron, val number: Int? = null)

/**
 * Players as ui.js playerRow() lists them; [compact] rows are 56dp. Tapping "Invite" turns it into "Invite sent";
 * [onTrail] hears taps on the trailing buttons (call, more, remove).
 */
class PlayerRowAdapter(
    items: List<PlayerRow>,
    private val compact: Boolean = false,
    private val onClick: ((PlayerRow) -> Unit)? = null,
    private val onTrail: ((PlayerRow, Trail) -> Unit)? = null,
) : RecyclerView.Adapter<BindingHolder<ItemPlayerRowBinding>>() {

    private val items = items.toMutableList()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BindingHolder(parent.inflateRow(ItemPlayerRowBinding::inflate))

    override fun onBindViewHolder(holder: BindingHolder<ItemPlayerRowBinding>, position: Int) {
        val p = items[position]
        val b = holder.binding
        val res = b.root.resources
        if (compact) {
            b.root.minimumHeight = res.getDimensionPixelSize(R.dimen.row_height_compact)
            val v = (4 * res.displayMetrics.density).toInt()
            b.root.setPadding(b.root.paddingLeft, v, b.root.paddingRight, v)
        }
        b.number.isVisible = p.number != null
        b.number.text = p.number?.toString()
        b.avatar.avatar(p.name)
        b.name.text = p.name
        b.meta.isVisible = p.meta != null
        b.meta.text = p.meta

        val t = p.trail
        b.chevron.isVisible = t == Trail.Chevron
        b.stat.isVisible = t is Trail.Stat
        b.stat.text = (t as? Trail.Stat)?.value
        b.check.isVisible = t is Trail.Check
        if (t is Trail.Check) b.check.isChecked = t.on
        b.invite.isVisible = t == Trail.Invite
        b.invite.setOnClickListener {
            val at = holder.bindingAdapterPosition
            if (at == RecyclerView.NO_POSITION) return@setOnClickListener
            items[at] = items[at].copy(trail = Trail.Pending)
            notifyItemChanged(at)
        }
        b.pending.isVisible = t == Trail.Pending
        b.call.isVisible = t == Trail.Call || t == Trail.CallAndMore
        b.more.isVisible = t == Trail.CallAndMore
        b.remove.isVisible = t == Trail.Remove
        listOf(b.call to Trail.Call, b.more to Trail.CallAndMore, b.remove to Trail.Remove).forEach { (button, which) ->
            button.setOnClickListener { onTrail?.invoke(p, which) }
        }
        b.cards.isVisible = t is Trail.Cards
        if (t is Trail.Cards) {
            b.yellow.text = t.yellow.toString()
            b.redMark.isVisible = t.red > 0
            b.red.isVisible = t.red > 0
            b.red.text = t.red.toString()
        }
        clickable(b.root, p, onClick)
    }
}

/** A team in a list: its code, an optional meta line, and a chevron or a status chip on the right. */
data class TeamRow(
    val code: String,
    val meta: String? = null,
    val status: StatusKind? = null,
    val statusText: String? = null,
    val chevron: Boolean = status == null,
)

/** Teams as ui.js teamRow() lists them. */
class TeamRowAdapter(
    private val items: List<TeamRow>,
    private val onClick: ((TeamRow) -> Unit)? = null,
) : RecyclerView.Adapter<BindingHolder<ItemTeamRowBinding>>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BindingHolder(parent.inflateRow(ItemTeamRowBinding::inflate))

    override fun onBindViewHolder(holder: BindingHolder<ItemTeamRowBinding>, position: Int) {
        val t = items[position]
        val b = holder.binding
        b.crest.crest(t.code)
        b.name.text = SampleData.teamName(t.code)
        b.meta.isVisible = t.meta != null
        b.meta.text = t.meta
        b.chevron.isVisible = t.chevron
        b.status.isVisible = t.status != null
        if (t.status != null) b.status.status(t.status, t.statusText.orEmpty())
        clickable(b.root, t, onClick)
    }
}
