package com.leaguex.app.start

import android.os.Bundle
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.databinding.ActivityGetStartedBinding
import com.leaguex.app.kit.bind

/** 06 Get started: the four ways into the app. Each card opens the screen its title names. */
class GetStartedActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityGetStartedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fitSystemBars(top = binding.root, bottom = binding.content)

        binding.follow.bind(R.drawable.ic_stadium, R.string.start_follow, R.string.start_follow_body) { go(SetUpProfileActivity::class) }
        binding.join.bind(R.drawable.ic_group_add, R.string.start_join, R.string.start_join_body) { }
        binding.register.bind(R.drawable.ic_shield, R.string.start_register, R.string.start_register_body) { }
        binding.run.bind(R.drawable.ic_emoji_events, R.string.start_run, R.string.start_run_body) { }
    }
}
