package com.leaguex.app.news

import android.os.Bundle
import androidx.core.view.isVisible
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityAnnouncementBinding
import com.leaguex.app.kit.avatar
import com.leaguex.app.kit.keepTogether
import com.leaguex.app.kit.rows.MatchRowAdapter
import com.leaguex.app.kit.rows.showRows
import com.leaguex.app.league.MatchActivity
import com.leaguex.app.league.MatchVersion

/** 22 Announcement detail: "Matchday 5 kick-off times", with the matches it lists. */
class AnnouncementActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityAnnouncementBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.content)
        backOn(b.top.back)
        b.top.title.setText(R.string.news_announcement)
        b.top.action.isVisible = true
        b.top.action.setIconResource(R.drawable.ic_share)
        b.top.action.contentDescription = getString(R.string.league_cd_share)

        val organizer = SampleData.LEAGUE.organizer
        b.headline.text = SampleData.ANNOUNCEMENTS.first().first
        b.avatar.avatar(organizer)
        b.byline.text = getString(R.string.news_byline, organizer)
        b.posted.text = keepTogether("Fri 14 Nov, 6:30 pm")
        b.matches.showRows(MatchRowAdapter(SampleData.MATCHDAY5) { go(MatchActivity::class, MatchVersion.BEFORE) })
    }
}
