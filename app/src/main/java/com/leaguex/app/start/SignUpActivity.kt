package com.leaguex.app.start

import android.os.Bundle
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivitySignUpBinding

/** 03 Sign up. Nothing is sent anywhere yet: Create account moves on to Get started. */
class SignUpActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fitSystemBars(top = binding.root, bottom = binding.footer)
        binding.top.title.setText(R.string.start_create_account)
        backOn(binding.top.back)

        binding.name.setText(SampleData.ME.name)
        binding.email.setText(SampleData.ME.email)
        binding.password.setText(SAMPLE_PASSWORD)

        binding.createAccount.setOnClickListener { go(GetStartedActivity::class) }
        binding.logIn.setOnClickListener { go(LogInActivity::class) }
    }
}

/** Ten characters, so the field shows the Figma's ten dots. */
internal const val SAMPLE_PASSWORD = "matchday05"
