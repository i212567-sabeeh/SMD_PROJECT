package com.leaguex.app.captains

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityEditTeamBinding
import com.leaguex.app.kit.crest
import com.leaguex.app.kit.teamColour
import kotlin.math.roundToInt

/** 31 Edit team: the logo, name, short code and colour. Save goes back to the team; nothing is sent anywhere yet. */
class EditTeamActivity : LeagueXActivity() {

    private lateinit var b: ActivityEditTeamBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityEditTeamBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.footer)
        b.top.title.setText(R.string.edit_team_title)
        backOn(b.top.back)

        val team = SampleData.TEAMS.getValue("MAR")
        b.crest.crest(team.code)
        b.name.setText(team.name)
        b.code.setText(team.code)
        swatches(selected = team.code)
        b.save.setOnClickListener { finish() }
    }

    /** One 36dp circle per team colour, 12dp apart; the [selected] one has a blue ring 3dp outside it. */
    private fun swatches(selected: String) {
        val dp = resources.displayMetrics.density
        fun px(value: Int) = (value * dp).roundToInt()
        b.swatches.removeAllViews()
        for (code in COLOURS) {
            val holder = FrameLayout(this).apply {
                if (code == selected) setBackgroundResource(R.drawable.bg_swatch_ring)
                contentDescription = SampleData.teamName(code)
                setOnClickListener { swatches(code) }
            }
            holder.addView(
                View(this).apply {
                    setBackgroundResource(R.drawable.bg_glyph_circle)
                    backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, teamColour(code)))
                },
                FrameLayout.LayoutParams(px(36), px(36), Gravity.CENTER),
            )
            b.swatches.addView(holder, LinearLayout.LayoutParams(px(46), px(46)).apply { marginEnd = px(2) })
        }
    }

    private companion object {
        /** The team colours, Margalla Strikers' first, as 31 Edit team lists them. */
        val COLOURS = listOf("MAR", "CAP", "POT", "RAW", "SAD", "KHY", "PIN", "KOH")
    }
}
