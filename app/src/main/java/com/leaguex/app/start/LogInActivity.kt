package com.leaguex.app.start

import android.os.Bundle
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityLogInBinding

/** 04 Log in. Nothing is checked yet: Log in opens Home. */
class LogInActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fitSystemBars(top = binding.root, bottom = binding.footer)
        binding.top.title.setText(R.string.start_log_in)
        backOn(binding.top.back)

        binding.email.setText(SampleData.ME.email)
        binding.password.setText(SAMPLE_PASSWORD)

        binding.forgot.setOnClickListener { go(ForgotPasswordActivity::class, ForgotPasswordVersion.FORM) }
        binding.createAccount.setOnClickListener { go(SignUpActivity::class) }
    }
}
