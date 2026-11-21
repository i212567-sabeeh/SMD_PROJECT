package com.leaguex.app.organizer

import androidx.annotation.StringRes
import com.leaguex.app.R
import com.leaguex.app.databinding.IncludeStepsBinding
import com.leaguex.app.databinding.IncludeTopBackBinding
import com.leaguex.app.kit.bind

/** What the organizer is creating, chosen on 32 Create / Type and carried through the steps as their version. */
enum class CreateKind { LEAGUE, TOURNAMENT }

/** The four steps' titles, as 32–35 show them. */
@StringRes
internal fun stepTitle(step: Int): Int = when (step) {
    1 -> R.string.create_step_type
    2 -> R.string.create_step_details
    3 -> R.string.create_step_rules
    else -> R.string.create_step_review
}

/** A create step's top: the close button, "Create", and "Step [step] of 4" over the bars. */
internal fun createTop(top: IncludeTopBackBinding, steps: IncludeStepsBinding, step: Int) {
    val context = top.root.context
    top.title.setText(R.string.create_title)
    top.back.setIconResource(R.drawable.ic_close)
    top.back.contentDescription = context.getString(R.string.create_cd_close)
    steps.bind(step, context.getString(stepTitle(step)))
}
