package com.leaguex.app.news

import android.os.Bundle
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityAnnouncementsBinding
import com.leaguex.app.databinding.IncludeNoticeCardBinding
import com.leaguex.app.kit.keepTogether

/** 21 Announcements: the league's announcements, newest first. Each opens 22 Announcement detail. */
class AnnouncementsActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityAnnouncementsBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.content)
        backOn(b.top.back)
        b.top.title.setText(R.string.news_announcements)
        for ((title, body, byline) in SampleData.ANNOUNCEMENTS) {
            val card = IncludeNoticeCardBinding.inflate(layoutInflater, b.content, true)
            card.title.text = keepTogether(title)
            card.body.text = keepTogether(body)
            card.byline.text = keepTogether(byline)
            card.root.setOnClickListener { go(AnnouncementActivity::class) }
        }
    }
}
