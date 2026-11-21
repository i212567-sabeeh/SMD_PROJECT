package com.leaguex.app.organizer

import android.os.Bundle
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityTeamRequestsBinding
import com.leaguex.app.databinding.IncludeRequestCardBinding
import com.leaguex.app.kit.crest
import com.leaguex.app.kit.keepTogether

/** 38 Team requests: approve or decline each team's request to enter the Islamabad Winter Cup. */
class TeamRequestsActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityTeamRequestsBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.content)
        b.top.title.setText(R.string.requests_title)
        backOn(b.top.back)

        val cup = SampleData.WINTER
        b.about.text = getString(R.string.requests_about, cup.name, cup.starts)
        for ((code, sent) in listOf("SAD" to "Thu 13 Nov", "PIN" to "Fri 14 Nov", "KOH" to "Fri 14 Nov")) {
            val team = SampleData.TEAMS.getValue(code)
            val card = IncludeRequestCardBinding.inflate(layoutInflater, b.requests, true)
            card.crest.crest(code)
            card.name.text = team.name
            card.meta.text = getString(R.string.requests_meta, team.captain, keepTogether(sent))
            // Answering a request takes it off the list.
            card.approve.setOnClickListener { b.requests.removeView(card.root) }
            card.decline.setOnClickListener { b.requests.removeView(card.root) }
        }
        b.code.text = cup.code
    }
}
