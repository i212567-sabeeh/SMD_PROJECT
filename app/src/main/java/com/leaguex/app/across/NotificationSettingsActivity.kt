package com.leaguex.app.across

import android.os.Bundle
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.databinding.ActivityNotificationSettingsBinding
import com.leaguex.app.kit.bind

/** 56 Notification settings: one switch per kind of notification, for every league and tournament you follow. */
class NotificationSettingsActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityNotificationSettingsBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.content)
        b.top.title.setText(R.string.notify_title)
        backOn(b.top.back)

        b.reminders.bind(R.drawable.ic_alarm, R.string.notify_reminders, on = true)
        b.scores.bind(R.drawable.ic_sports_soccer, R.string.notify_scores, on = true)
        b.kickoff.bind(R.drawable.ic_timer, R.string.notify_kickoff, on = true)
        b.schedule.bind(R.drawable.ic_event, R.string.notify_schedule, on = true)
        b.announcements.bind(R.drawable.ic_campaign, R.string.notify_announcements, on = true)
        b.playoffs.bind(R.drawable.ic_emoji_events, R.string.notify_playoffs, on = false)
    }
}
