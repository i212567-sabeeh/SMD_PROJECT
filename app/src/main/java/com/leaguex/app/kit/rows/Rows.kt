package com.leaguex.app.kit.rows

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.leaguex.app.R
import kotlin.math.roundToInt

/**
 * Shows [adapter]'s rows as a list that sizes to its content inside a scrolling screen (kit.css .list). Rows are
 * divided by a 1dp line, except timelines ([divided] false). A list with a card background clips its rows to the
 * card's corners.
 */
fun RecyclerView.showRows(adapter: RecyclerView.Adapter<*>, divided: Boolean = true) {
    layoutManager = LinearLayoutManager(context)
    this.adapter = adapter
    isNestedScrollingEnabled = false
    if (divided && itemDecorationCount == 0) addItemDecoration(LineDivider(context))
    if (background != null) clipToOutline = true
}

/** The 1dp line between rows (`.list > .row + .row { border-top }`). */
class LineDivider(context: Context) : RecyclerView.ItemDecoration() {
    private val paint = Paint().apply { color = ContextCompat.getColor(context, R.color.line) }
    private val size = context.resources.displayMetrics.density.roundToInt().coerceAtLeast(1)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.getChildAdapterPosition(view) > 0) outRect.top = size
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (parent.getChildAdapterPosition(child) <= 0) continue
            val top = child.top - size
            c.drawRect(0f, top.toFloat(), parent.width.toFloat(), (top + size).toFloat(), paint)
        }
    }
}

/** A row's view holder: its view binding. */
class BindingHolder<B : ViewBinding>(val binding: B) : RecyclerView.ViewHolder(binding.root)

/** Inflates a row's binding into [parent] without attaching it. */
internal inline fun <B : ViewBinding> ViewGroup.inflateRow(inflate: (LayoutInflater, ViewGroup, Boolean) -> B): B =
    inflate(LayoutInflater.from(context), this, false)

/** Makes [view] tappable with [onClick], or plain (no ripple, not focusable) when there's nothing to do. */
internal fun <T> clickable(view: View, item: T, onClick: ((T) -> Unit)?) {
    if (onClick == null) {
        view.setOnClickListener(null)
        view.isClickable = false
        view.isFocusable = false
    } else {
        view.setOnClickListener { onClick(item) }
    }
}
