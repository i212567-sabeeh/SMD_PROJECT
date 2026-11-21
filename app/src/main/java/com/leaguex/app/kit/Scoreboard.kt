package com.leaguex.app.kit

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import com.leaguex.app.R
import com.leaguex.app.databinding.IncludeScoreboardBinding
import kotlin.math.roundToInt

/** Where a match is, for its scoreboard (ui.js scoreboard states). */
enum class ScoreState { UPCOMING, LIVE, HALF_TIME, FULL_TIME, PENALTIES }

/**
 * A match on its scoreboard. Before kick-off [status] is the kick-off time ("7:00 pm"); once it's under way the
 * score shows, with [status] under it: the live chip's text while LIVE ("Live 32'"), otherwise a label such as
 * "Full time". [detail] is an optional line under that ("Sat 15 Nov, Court 2").
 */
data class Scoreboard(
    val home: String,
    val homeName: CharSequence,
    val away: String,
    val awayName: CharSequence,
    val state: ScoreState,
    val status: CharSequence,
    val homeScore: Int? = null,
    val awayScore: Int? = null,
    val detail: CharSequence? = null,
)

/** Shows [sb] as ui.js scoreboard() draws it. */
fun IncludeScoreboardBinding.bind(sb: Scoreboard) {
    homeCrest.crest(sb.home)
    homeName.text = sb.homeName
    awayCrest.crest(sb.away)
    awayName.text = sb.awayName

    val upcoming = sb.state == ScoreState.UPCOMING
    TextViewCompat.setTextAppearance(
        score,
        if (upcoming) R.style.TextAppearance_LeagueX_Display_Title else R.style.TextAppearance_LeagueX_Display_Score,
    )
    score.setTextColor(ContextCompat.getColor(root.context, R.color.on_blue))
    // Anton placed as CSS places it (styles_anton.xml): no font padding, pulled in by half the leading.
    score.includeFontPadding = false
    val halfLeading = ((if (upcoming) -3 else -10) * root.resources.displayMetrics.density).roundToInt()
    score.updateLayoutParams<ViewGroup.MarginLayoutParams> {
        topMargin = halfLeading
        bottomMargin = halfLeading
    }
    score.text = if (upcoming) sb.status else "${sb.homeScore ?: 0}–${sb.awayScore ?: 0}"

    live.isVisible = sb.state == ScoreState.LIVE
    live.text = sb.status
    label.isVisible = !upcoming && sb.state != ScoreState.LIVE
    label.text = sb.status
    detail.isVisible = sb.detail != null
    detail.text = sb.detail
}
