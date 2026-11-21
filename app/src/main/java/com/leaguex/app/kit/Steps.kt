package com.leaguex.app.kit

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.leaguex.app.R
import com.leaguex.app.databinding.IncludeStepsBinding

/** Shows "Step [step] of 4: [title]" over the four bars, the first [step] of them blue (ui.js steps). */
fun IncludeStepsBinding.bind(step: Int, title: CharSequence) {
    val bars = listOf(bar1, bar2, bar3, bar4)
    val context = root.context
    label.text = context.getString(R.string.kit_steps_label, step, bars.size, title)
    bars.forEachIndexed { i, bar ->
        bar.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, if (i < step) R.color.blue else R.color.line))
    }
}
