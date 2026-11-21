package com.leaguex.app.kit.rows

import android.graphics.Color
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.data.TableRow
import com.leaguex.app.databinding.ItemTableHeadBinding
import com.leaguex.app.databinding.ItemTableRowBinding
import com.leaguex.app.kit.crest

/**
 * The league table (ui.js tableHead and tableRow): the heading, then one row per team. [short] drops W, D and L
 * (12 League page). The [highlight] team's row is tinted, and a dashed line follows the row at [playoffAfter].
 */
class TableAdapter(
    private val rows: List<TableRow>,
    private val short: Boolean = false,
    private val highlight: String? = null,
    private val playoffAfter: Int? = null,
    private val onClick: ((TableRow) -> Unit)? = null,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount() = rows.size + 1

    override fun getItemViewType(position: Int) = if (position == 0) HEAD else ROW

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == HEAD) BindingHolder(parent.inflateRow(ItemTableHeadBinding::inflate))
        else BindingHolder(parent.inflateRow(ItemTableRowBinding::inflate))

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) {
            val head = (holder as BindingHolder<ItemTableHeadBinding>).binding
            listOf(head.w, head.d, head.l).forEach { it.isVisible = !short }
            return
        }
        val r = rows[position - 1]
        val b = (holder as BindingHolder<ItemTableRowBinding>).binding
        b.pos.text = r.pos.toString()
        b.crest.crest(r.code)
        b.teamName.text = SampleData.teamName(r.code)
        b.p.text = r.p.toString()
        b.w.text = r.w.toString()
        b.d.text = r.d.toString()
        b.l.text = r.l.toString()
        listOf(b.w, b.d, b.l).forEach { it.isVisible = !short }
        b.gd.text = goalDifference(r.gd)
        b.pts.text = r.pts.toString()
        b.root.setBackgroundColor(if (r.code == highlight) ContextCompat.getColor(b.root.context, R.color.tint) else Color.TRANSPARENT)
        b.playoffLine.isVisible = playoffAfter == r.pos
        clickable(b.root, r, onClick)
    }

    private companion object {
        const val HEAD = 0
        const val ROW = 1
    }
}

/** "+7", "0" or "−8", with a true minus sign as ui.js writes it. */
fun goalDifference(gd: Int): String = when {
    gd > 0 -> "+$gd"
    gd < 0 -> "−${-gd}"
    else -> "0"
}
