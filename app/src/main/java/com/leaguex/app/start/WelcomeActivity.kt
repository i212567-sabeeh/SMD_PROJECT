package com.leaguex.app.start

import android.os.Bundle
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityWelcomeBinding
import com.leaguex.app.kit.crest
import com.leaguex.app.kit.rows.TableAdapter
import com.leaguex.app.kit.rows.showRows

/** 02 Welcome: a taste of the app (the next match and the top of the table), then Create account or Log in. */
class WelcomeActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fitSystemBars(top = binding.root, bottom = binding.footer)

        val next = SampleData.MATCHDAY5.first { it.home == "MAR" }
        binding.matchday.text = getString(R.string.start_matchday, SampleData.NEXT_MATCHDAY)
        binding.homeCrest.crest(next.home)
        binding.homeCode.text = next.home
        binding.awayCode.text = next.away
        binding.awayCrest.crest(next.away)
        binding.`when`.text = listOf(SampleData.MATCHDAY5_DATE, next.time, next.venue).joinToString(", ")
        binding.table.showRows(TableAdapter(SampleData.TABLE.take(2), short = true, highlight = "MAR", head = false))
        binding.hero.clipToOutline = true

        binding.createAccount.setOnClickListener { go(SignUpActivity::class) }
        binding.logIn.setOnClickListener { go(LogInActivity::class) }
    }
}
