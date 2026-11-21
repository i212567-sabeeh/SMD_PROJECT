package com.leaguex.app.organizer

import android.os.Bundle
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.databinding.ActivityPostAnnouncementBinding
import com.leaguex.app.kit.bind
import com.leaguex.app.news.AnnouncementsActivity

/** 43 Post announcement: a title and message for followers, with an optional push. Post opens the announcements. */
class PostAnnouncementActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityPostAnnouncementBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.footer)
        b.top.title.setText(R.string.post_title)
        b.top.back.setIconResource(R.drawable.ic_close)
        b.top.back.contentDescription = getString(R.string.create_cd_close)
        backOn(b.top.back)

        b.headline.setText("Matchday 6 moves to Court 1")
        b.message.setText("Court 2 is closed for maintenance. All matchday 6 games are on Court 1 at the same times.")
        b.push.bind(R.string.post_push, R.string.post_push_body, on = true)
        b.audience.setText(resources.getStringArray(R.array.audiences).first(), false)
        b.post.setOnClickListener {
            go(AnnouncementsActivity::class)
            finish()
        }
    }
}
