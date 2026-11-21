package com.leaguex.app.kit

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMarginsRelative
import androidx.core.widget.TextViewCompat
import com.leaguex.app.R
import com.leaguex.app.databinding.IncludeTieBinding
import kotlin.math.roundToInt

/**
 * A bracket tie (ui.js tie). A null [a] or [b] is a team still to be decided, shown as [labelA] or [labelB], or as
 * "To be decided". [winner] is the team that went through on penalties after a level score, drawn bold like a win.
 */
data class Tie(
    val a: String?,
    val b: String?,
    val scoreA: Int? = null,
    val scoreB: Int? = null,
    val note: CharSequence? = null,
    val winner: String? = null,
    val labelA: CharSequence? = null,
    val labelB: CharSequence? = null,
)

/** Shows [tie] 164dp wide, or 110dp wide and tighter when [narrow], as the bracket's three rounds need. */
fun IncludeTieBinding.bind(tie: Tie, narrow: Boolean = false) {
    val dp = root.resources.displayMetrics.density
    fun px(value: Int) = (value * dp).roundToInt()
    root.updateLayoutParams<ViewGroup.LayoutParams> { width = px(if (narrow) 110 else 164) }
    root.setPaddingRelative(px(if (narrow) 10 else 12), px(if (narrow) 8 else 10), px(if (narrow) 10 else 12), px(if (narrow) 8 else 10))
    root.dividerDrawable = ContextCompat.getDrawable(root.context, if (narrow) R.drawable.gap_4 else R.drawable.gap_6)

    val wonA = won(tie.scoreA, tie.scoreB) || (tie.winner != null && tie.winner == tie.a)
    val wonB = won(tie.scoreB, tie.scoreA) || (tie.winner != null && tie.winner == tie.b)
    line(lineA, crestA, codeA, goalsA, tie.a, tie.scoreA, wonA, tie.labelA, narrow, px(if (narrow) 6 else 8), px(28))
    line(lineB, crestB, codeB, goalsB, tie.b, tie.scoreB, wonB, tie.labelB, narrow, px(if (narrow) 6 else 8), px(28))

    note.text = tie.note
    note.isVisible = tie.note != null
}

private fun won(score: Int?, other: Int?) = score != null && other != null && score > other

private fun line(
    line: View, crest: TextView, code: TextView, goals: TextView,
    team: String?, score: Int?, won: Boolean, label: CharSequence?, narrow: Boolean, gap: Int, crestSize: Int,
) {
    val context = code.context
    code.updateLayoutParams<ViewGroup.MarginLayoutParams> { updateMarginsRelative(start = gap) }
    goals.updateLayoutParams<ViewGroup.MarginLayoutParams> { updateMarginsRelative(start = gap) }
    line.minimumHeight = 0
    if (team != null) {
        crest.isVisible = true
        crest.crest(team)
        code.text = team
        TextViewCompat.setTextAppearance(code, if (won) R.style.TextAppearance_LeagueX_TieCode_Won else R.style.TextAppearance_LeagueX_TieCode)
        goals.isVisible = score != null
        goals.text = score?.toString()
        return
    }
    // Still to be decided: a muted label, beside a line-coloured crest, or alone in a narrow tie.
    goals.isVisible = false
    code.text = label ?: context.getString(R.string.kit_tie_to_be_decided)
    if (narrow) {
        crest.isVisible = false
        line.minimumHeight = crestSize
        code.updateLayoutParams<ViewGroup.MarginLayoutParams> { updateMarginsRelative(start = 0) }
        TextViewCompat.setTextAppearance(code, R.style.TextAppearance_LeagueX_Body_Small)
    } else {
        crest.isVisible = true
        crest.text = null
        crest.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.line))
        TextViewCompat.setTextAppearance(code, R.style.TextAppearance_LeagueX_TieCode)
    }
    code.setTextColor(ContextCompat.getColor(context, R.color.muted))
}
