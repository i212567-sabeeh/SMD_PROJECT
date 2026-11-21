package com.leaguex.app.kit

import android.content.res.ColorStateList
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.leaguex.app.R
import com.leaguex.app.databinding.IncludeKicksRowBinding
import kotlin.math.roundToInt

/**
 * A team's penalty kicks (ui.js kicksRow): [code]'s crest and code, one circle per kick (true scored, false missed,
 * null still to take) and their [total].
 */
fun IncludeKicksRowBinding.bind(code: String, kicks: List<Boolean?>, total: Int) {
    crest.crest(code)
    this.code.text = code
    this.total.text = total.toString()
    val size = (32 * root.resources.displayMetrics.density).roundToInt()
    this.kicks.removeAllViews()
    for (kick in kicks) this.kicks.addView(kickView(this.kicks, kick), LinearLayout.LayoutParams(size, size))
}

/** A taken kick is a tinted circle with a 20dp check or cross; one still to take is a dashed ring. */
private fun kickView(parent: LinearLayout, kick: Boolean?): ImageView = ImageView(parent.context).apply {
    val context = parent.context
    if (kick == null) {
        setBackgroundResource(R.drawable.bg_kick_open)
        contentDescription = context.getString(R.string.kit_kick_to_take)
        return@apply
    }
    setBackgroundResource(R.drawable.bg_kick)
    backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, if (kick) R.color.success_bg else R.color.error_bg))
    setImageResource(if (kick) R.drawable.ic_check else R.drawable.ic_close)
    imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, if (kick) R.color.success else R.color.error))
    val inset = (6 * context.resources.displayMetrics.density).roundToInt()
    setPadding(inset, inset, inset, inset)
    contentDescription = context.getString(if (kick) R.string.kit_kick_scored else R.string.kit_kick_missed)
}
