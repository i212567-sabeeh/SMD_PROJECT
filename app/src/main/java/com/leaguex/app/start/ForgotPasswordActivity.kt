package com.leaguex.app.start

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityForgotPasswordBinding
import com.leaguex.app.kit.bind
import com.leaguex.app.screen

/** 05 Forgot password's versions: the email form, and "Check your email" once the link is sent. */
enum class ForgotPasswordVersion { FORM, CHECK_EMAIL }

/** 05 Forgot password. Send reset link shows "Check your email"; nothing is emailed yet. */
class ForgotPasswordActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fitSystemBars(top = binding.root, bottom = binding.footer)
        binding.top.title.setText(R.string.start_reset_title)
        backOn(binding.top.back)

        val sent = versionOr(ForgotPasswordVersion.FORM) == ForgotPasswordVersion.CHECK_EMAIL
        binding.form.isVisible = !sent
        binding.sendLink.isVisible = !sent
        binding.sent.isVisible = sent
        binding.backToLogIn.isVisible = sent
        binding.sendAgainRow.isVisible = sent

        binding.email.setText(SampleData.ME.email)
        binding.empty.bind(
            R.drawable.ic_mark_email_read,
            getString(R.string.start_check_title),
            getString(R.string.start_check_body, SampleData.ME.email),
        )

        binding.sendLink.setOnClickListener { go(ForgotPasswordActivity::class, ForgotPasswordVersion.CHECK_EMAIL) }
        binding.backToLogIn.setOnClickListener {
            startActivity(screen(LogInActivity::class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
            finish()
        }
    }
}
