package com.leaguex.app.organizer

import android.os.Bundle
import androidx.core.view.isVisible
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.databinding.ActivityCreateRulesBinding
import com.leaguex.app.kit.bind

/** 34 Create / Rules: a league's fixtures, points and playoffs, or a knockout's extra time and third-place match. */
class CreateRulesActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityCreateRulesBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.footer)
        createTop(b.top, b.steps, step = 3)
        backOn(b.top.back)

        val kind = versionOr(CreateKind.LEAGUE)
        val league = kind == CreateKind.LEAGUE
        listOf(b.playEach, b.points, b.playoffsCard).forEach { it.isVisible = league }
        listOf(b.extraTimeCard, b.penaltiesNote, b.thirdPlaceCard).forEach { it.isVisible = !league }

        b.win.setText("3")
        b.draw.setText("1")
        b.loss.setText("0")
        b.playoffs.bind(R.string.rules_playoffs, R.string.rules_playoffs_body, on = true)
        b.extraTime.bind(R.string.rules_extra_time, R.string.rules_extra_time_body, on = true)
        b.extraLength.setText(resources.getStringArray(R.array.extra_time_lengths).first(), false)
        b.thirdPlace.bind(R.string.rules_third_place, R.string.rules_third_place_body, on = false)
        b.matchLength.setText(resources.getStringArray(R.array.match_lengths).first(), false)
        b.next.setOnClickListener { go(CreateReviewActivity::class, kind) }
    }
}
