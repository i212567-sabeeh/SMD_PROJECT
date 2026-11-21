package com.leaguex.app.league

import android.os.Bundle
import androidx.core.view.isVisible
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityLeagueBinding
import com.leaguex.app.kit.bind
import com.leaguex.app.kit.keepTogether
import com.leaguex.app.kit.rows.MatchRowAdapter
import com.leaguex.app.kit.rows.showRows
import com.leaguex.app.news.AnnouncementActivity

/** 12 League page's versions: the page, and 57 Error / No connection when it can't load. */
enum class LeagueVersion { DEFAULT, NO_CONNECTION }

/** 12 League page: what someone sees of a league before following it. */
class LeagueActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityLeagueBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.content)
        backOn(b.top.back)
        val league = SampleData.LEAGUE

        if (versionOr(LeagueVersion.DEFAULT) == LeagueVersion.NO_CONNECTION) {
            b.top.title.text = league.name
            b.page.isVisible = false
            b.error.isVisible = true
            b.empty.bind(
                R.drawable.ic_wifi_off, getString(R.string.error_no_connection_title),
                getString(R.string.error_no_connection_body), getString(R.string.error_try_again),
            )
            b.empty.button.setOnClickListener {
                go(LeagueActivity::class)
                finish()
            }
            return
        }

        b.top.title.setText(R.string.league_title)
        b.top.action.isVisible = true
        b.top.action.setIconResource(R.drawable.ic_share)
        b.top.action.contentDescription = getString(R.string.league_cd_share)

        b.name.text = league.name
        b.place.text = league.place
        b.organizer.text = getString(R.string.league_organized_by, league.organizer)
        b.teams.text = SampleData.TEAMS.size.toString()
        b.season.text = league.season.substringAfter(' ')
        b.matchday.text = getString(R.string.league_matchday_of, SampleData.NEXT_MATCHDAY, MATCHDAYS)

        b.follow.setOnClickListener {
            b.follow.setText(R.string.league_following_button)
            b.follow.setIconResource(R.drawable.ic_check)
        }

        b.nextHead.title.setText(R.string.league_next_matches)
        val next = SampleData.MATCHDAY5.filter { it.home == "MAR" || it.home == "SAD" }
        b.next.showRows(MatchRowAdapter(next) { go(MatchActivity::class, MatchVersion.BEFORE) })

        b.announcement.bind(
            R.drawable.ic_campaign, getString(R.string.home_notice, SampleData.NEXT_MATCHDAY),
            keepTogether("${league.organizer}, Fri 14 Nov"),
        ) { go(AnnouncementActivity::class) }
    }

    private companion object {
        /** Matchdays in the Islamabad Futsal League's season. */
        const val MATCHDAYS = 14
    }
}
