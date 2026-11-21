package com.leaguex.app.screens.frames

import com.leaguex.app.screen
import com.leaguex.app.screens.Frame
import com.leaguex.app.start.ForgotPasswordActivity
import com.leaguex.app.start.ForgotPasswordVersion
import com.leaguex.app.start.GetStartedActivity
import com.leaguex.app.start.LogInActivity
import com.leaguex.app.start.SetUpProfileActivity
import com.leaguex.app.start.SignUpActivity
import com.leaguex.app.start.SplashActivity
import com.leaguex.app.start.SplashVersion
import com.leaguex.app.start.WelcomeActivity

/** Frames of the Figma section "Getting started (1–7)" (slug 01-getting-started), in the Figma's order. */
val startFrames: List<Frame> = listOf(
    Frame("s01-splash", "01 Splash") { it.screen(SplashActivity::class, SplashVersion.STILL) },
    Frame("s02-welcome", "02 Welcome") { it.screen(WelcomeActivity::class) },
    Frame("s03-sign-up", "03 Sign up") { it.screen(SignUpActivity::class) },
    Frame("s04-log-in", "04 Log in") { it.screen(LogInActivity::class) },
    Frame("s05-forgot-password", "05 Forgot password") { it.screen(ForgotPasswordActivity::class, ForgotPasswordVersion.FORM) },
    Frame("s05-check-your-email", "05 Forgot password / Check your email") {
        it.screen(ForgotPasswordActivity::class, ForgotPasswordVersion.CHECK_EMAIL)
    },
    Frame("s06-get-started", "06 Get started") { it.screen(GetStartedActivity::class) },
    Frame("s07-set-up-profile", "07 Set up your profile") { it.screen(SetUpProfileActivity::class) },
)
