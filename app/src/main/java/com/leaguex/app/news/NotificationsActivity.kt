package com.leaguex.app.news

import android.os.Bundle
import androidx.core.view.isVisible
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.databinding.ActivityNotificationsBinding
import com.leaguex.app.kit.rows.NoteRow
import com.leaguex.app.kit.rows.NoteRowAdapter
import com.leaguex.app.kit.rows.showRows
import com.leaguex.app.league.MatchActivity
import com.leaguex.app.league.MatchVersion

/** 23 Notifications: new ones first, then earlier ones. Mark all read clears the new ones' dots. */
class NotificationsActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.content)
        backOn(b.top.back)
        b.top.title.setText(R.string.notes_title)
        b.top.actionText.isVisible = true
        b.top.actionText.setText(R.string.notes_mark_all_read)

        val fresh = listOf(
            NoteRow(R.drawable.ic_alarm, "Kick-off in 1 hour: MAR vs RAW at 7:00 pm, Court 2", time = "Today, 6:00 pm", unread = true),
            NoteRow(R.drawable.ic_sports_soccer, "Goal: Zain Abbas scores for Margalla Strikers, 1–0", time = "Today, 7:06 pm", unread = true),
        )
        val openMatch: (NoteRow) -> Unit = { go(MatchActivity::class, MatchVersion.LIVE) }
        b.fresh.showRows(NoteRowAdapter(fresh, openMatch))
        b.earlier.showRows(
            NoteRowAdapter(
                listOf(
                    NoteRow(R.drawable.ic_event, "Schedule change: KHY vs KOH moved to 5:30 pm", time = "Fri 14 Nov"),
                    NoteRow(R.drawable.ic_emoji_events, "Capital Futsal Cup draw is out", time = "Mon 27 Oct"),
                ),
            ),
        )
        b.top.actionText.setOnClickListener {
            b.fresh.showRows(NoteRowAdapter(fresh.map { it.copy(unread = false) }, openMatch))
        }
    }
}
