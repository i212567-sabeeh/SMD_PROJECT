package com.leaguex.app.kit.rows

import android.view.Gravity
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.leaguex.app.R
import com.leaguex.app.data.EventType
import com.leaguex.app.data.MatchEvent
import com.leaguex.app.data.MatchRow
import com.leaguex.app.data.MatchState
import com.leaguex.app.data.SampleData
import com.leaguex.app.data.Side
import com.leaguex.app.databinding.ItemEventBinding
import com.leaguex.app.databinding.ItemEventPeriodBinding
import com.leaguex.app.databinding.ItemMatchRowBinding
import com.leaguex.app.databinding.ItemResultRowBinding
import com.leaguex.app.kit.crest
import com.leaguex.app.kit.keepTogether

/** Matches as ui.js matchRow() lists them: when on the left, both teams on the right, the winner in bold. */
class MatchRowAdapter(
    private val items: List<MatchRow>,
    private val onClick: ((MatchRow) -> Unit)? = null,
) : RecyclerView.Adapter<BindingHolder<ItemMatchRowBinding>>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BindingHolder(parent.inflateRow(ItemMatchRowBinding::inflate))

    override fun onBindViewHolder(holder: BindingHolder<ItemMatchRowBinding>, position: Int) {
        val m = items[position]
        val b = holder.binding
        val context = b.root.context
        when (m.state) {
            MatchState.UPCOMING -> {
                b.whenTop.text = keepTogether(m.time.orEmpty())
                b.whenTop.setTextColor(ContextCompat.getColor(context, R.color.ink))
                b.whenBottom.text = m.venue
                b.whenBottom.isVisible = m.venue != null
                TextViewCompat.setTextAppearance(b.whenBottom, R.style.TextAppearance_LeagueX_Body_Small)
                b.whenBottom.setTextColor(ContextCompat.getColor(context, R.color.muted))
            }
            MatchState.LIVE -> {
                b.whenTop.text = m.minute
                b.whenTop.isVisible = m.minute != null
                b.whenTop.setTextColor(ContextCompat.getColor(context, R.color.orange_text))
                b.whenBottom.isVisible = true
                b.whenBottom.setText(R.string.row_live)
                TextViewCompat.setTextAppearance(b.whenBottom, R.style.TextAppearance_LeagueX_Label_Bold)
                b.whenBottom.setTextColor(ContextCompat.getColor(context, R.color.orange_text))
            }
            MatchState.RESULT -> {
                b.whenTop.setText(R.string.row_ft)
                b.whenTop.setTextColor(ContextCompat.getColor(context, R.color.muted))
                b.whenBottom.text = m.meta?.let(::keepTogether)
                b.whenBottom.isVisible = m.meta != null
                TextViewCompat.setTextAppearance(b.whenBottom, R.style.TextAppearance_LeagueX_Body_Small)
                b.whenBottom.setTextColor(ContextCompat.getColor(context, R.color.muted))
            }
        }
        if (m.state != MatchState.LIVE) b.whenTop.isVisible = true
        val goals = m.state != MatchState.UPCOMING
        val homeWon = m.state == MatchState.RESULT && (m.homeScore ?: 0) > (m.awayScore ?: 0)
        val awayWon = m.state == MatchState.RESULT && (m.awayScore ?: 0) > (m.homeScore ?: 0)
        b.homeCrest.crest(m.home)
        b.homeName.text = SampleData.teamName(m.home)
        TextViewCompat.setTextAppearance(b.homeName, if (homeWon) R.style.TextAppearance_LeagueX_Label_Large else R.style.TextAppearance_LeagueX_Body_Medium)
        b.homeGoals.isVisible = goals
        b.homeGoals.text = m.homeScore?.toString()
        b.awayCrest.crest(m.away)
        b.awayName.text = SampleData.teamName(m.away)
        TextViewCompat.setTextAppearance(b.awayName, if (awayWon) R.style.TextAppearance_LeagueX_Label_Large else R.style.TextAppearance_LeagueX_Body_Medium)
        b.awayGoals.isVisible = goals
        b.awayGoals.text = m.awayScore?.toString()
        b.updated.isVisible = m.updated != null
        b.updated.text = m.updated
        clickable(b.root, m, onClick)
    }
}

/** Results on one line (ui.js resultRow): crest, code, score, code, crest and "FT" or the row's [MatchRow.meta]. */
class ResultRowAdapter(
    private val items: List<MatchRow>,
    private val onClick: ((MatchRow) -> Unit)? = null,
) : RecyclerView.Adapter<BindingHolder<ItemResultRowBinding>>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BindingHolder(parent.inflateRow(ItemResultRowBinding::inflate))

    override fun onBindViewHolder(holder: BindingHolder<ItemResultRowBinding>, position: Int) {
        val m = items[position]
        val b = holder.binding
        b.homeCrest.crest(m.home)
        b.homeCode.text = m.home
        b.score.text = "${m.homeScore ?: 0}–${m.awayScore ?: 0}"
        b.awayCode.text = m.away
        b.awayCrest.crest(m.away)
        b.meta.text = m.meta?.let(::keepTogether) ?: b.root.context.getString(R.string.row_ft)
        clickable(b.root, m, onClick)
    }
}

/** A match's timeline (ui.js eventRow): home events read left to right, away events mirrored on the right. */
class EventAdapter(private val events: List<MatchEvent>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount() = events.size

    override fun getItemViewType(position: Int) = if (events[position].type == EventType.PERIOD) PERIOD else EVENT

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == PERIOD) BindingHolder(parent.inflateRow(ItemEventPeriodBinding::inflate))
        else BindingHolder(parent.inflateRow(ItemEventBinding::inflate))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val e = events[position]
        @Suppress("UNCHECKED_CAST")
        if (e.type == EventType.PERIOD) {
            (holder as BindingHolder<ItemEventPeriodBinding>).binding.text.text = e.player
            return
        }
        @Suppress("UNCHECKED_CAST")
        val b = (holder as BindingHolder<ItemEventBinding>).binding
        val context = b.root.context
        val away = e.side == Side.AWAY
        val order = if (away) listOf(b.what, b.mark, b.minute) else listOf(b.minute, b.mark, b.what)
        if (b.root.getChildAt(0) !== order[0]) {
            b.root.removeAllViews()
            order.forEach(b.root::addView)
        }
        b.what.gravity = if (away) Gravity.END else Gravity.START
        b.minute.text = e.minute
        when (e.type) {
            EventType.GOAL, EventType.SUB -> {
                b.icon.isVisible = true
                b.cardMark.isVisible = false
                b.icon.setImageResource(if (e.type == EventType.GOAL) R.drawable.ic_sports_soccer else R.drawable.ic_swap_vert)
                b.icon.imageTintList = ContextCompat.getColorStateList(context, if (e.type == EventType.GOAL) R.color.ink else R.color.blue)
            }
            else -> {
                b.icon.isVisible = false
                b.cardMark.isVisible = true
                b.cardMark.backgroundTintList = ContextCompat.getColorStateList(context, if (e.type == EventType.YELLOW) R.color.card_yellow else R.color.card_red)
            }
        }
        b.player.text = e.player
        b.player.gravity = if (away) Gravity.END else Gravity.START
        b.detail.text = e.detail
        b.detail.isVisible = e.detail != null
        b.detail.gravity = if (away) Gravity.END else Gravity.START
        b.waiting.isVisible = e.waiting
    }

    private companion object {
        const val EVENT = 0
        const val PERIOD = 1
    }
}
