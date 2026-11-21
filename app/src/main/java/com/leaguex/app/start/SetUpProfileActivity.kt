package com.leaguex.app.start

import android.os.Bundle
import androidx.core.view.isVisible
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivitySetUpProfileBinding
import com.leaguex.app.home.HomeActivity
import com.leaguex.app.home.HomeVersion

/** 07 Set up your profile: photo, positions, where you live and phone. Skip and Save and continue open Home. */
class SetUpProfileActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySetUpProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fitSystemBars(top = binding.root, bottom = binding.footer)
        binding.top.title.setText(R.string.start_profile_title)
        backOn(binding.top.back)
        binding.top.actionText.isVisible = true
        binding.top.actionText.setText(R.string.start_skip)

        binding.top.actionText.setOnClickListener { go(HomeActivity::class, HomeVersion.LEAGUE) }
        binding.save.setOnClickListener { go(HomeActivity::class, HomeVersion.LEAGUE) }
        binding.province.setText(SampleData.ME.province, false)
        binding.city.setText(SampleData.ME.city, false)
    }
}
