package com.leaguex.app.across

import android.os.Bundle
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityLockScreenBinding
import com.leaguex.app.kit.bind
import com.leaguex.app.league.MatchActivity
import com.leaguex.app.league.MatchVersion
import com.leaguex.app.news.AnnouncementActivity
import com.leaguex.app.news.NotificationsActivity

/**
 * 54 Push notifications: a look-alike of the phone's lock screen at 6:00 on matchday, showing LeagueX's
 * notifications. Tapping one opens what it's about: the match, the announcement, or the notifications list.
 */
class LockScreenActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityLockScreenBinding.inflate(layoutInflater)
        setContentView(b.root)
        statusIcons(onBlue = true)
        fitSystemBars(top = b.root, bottom = b.root)

        b.clock.text = CLOCK
        b.date.text = SampleData.MATCHDAY5_DATE
        b.kickoff.bind("Kick-off in 1 hour", "MAR vs RAW at 7:00 pm, Court 2", "6:00 pm")
        b.kickoff.root.setOnClickListener { go(MatchActivity::class, MatchVersion.BEFORE) }
        b.goal.bind("Goal: Imran Afridi scores for Khyber Eagles", "KHY 1–0 KOH, 12th minute", "5:42 pm")
        b.goal.root.setOnClickListener { go(NotificationsActivity::class) }
        b.announcement.bind("New announcement", "Matchday 5 kick-off times", "Fri")
        b.announcement.root.setOnClickListener { go(AnnouncementActivity::class) }
        b.schedule.bind("Schedule change", "Matchday 6: CAP vs MAR moved to 8:00 pm", "Fri")
        b.schedule.root.setOnClickListener { go(NotificationsActivity::class) }
    }

    private companion object {
        /** The time the Figma frame shows, an hour before kick-off. */
        const val CLOCK = "6:00"
    }
}
