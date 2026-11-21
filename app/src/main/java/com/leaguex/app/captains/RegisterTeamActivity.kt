package com.leaguex.app.captains

import android.os.Bundle
import androidx.core.view.isVisible
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityRegisterTeamBinding
import com.leaguex.app.kit.bind
import com.leaguex.app.kit.crest
import com.leaguex.app.kit.keepTogether
import com.leaguex.app.news.TeamActivity
import com.leaguex.app.news.TeamVersion

/** 28 Register a team's versions: the request form, and the request waiting for the organizer's approval. */
enum class RegisterTeamVersion { DEFAULT, WAITING }

/** 28 Register a team: a captain enters one of their teams in the Islamabad Winter Cup. */
class RegisterTeamActivity : LeagueXActivity() {

    private lateinit var b: ActivityRegisterTeamBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRegisterTeamBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.footer)
        b.top.title.setText(R.string.register_title)
        backOn(b.top.back)

        val team = SampleData.TEAMS.getValue("MAR")
        val cup = SampleData.WINTER
        val organizer = SampleData.LEAGUE.organizer

        if (versionOr(RegisterTeamVersion.DEFAULT) == RegisterTeamVersion.WAITING) {
            b.form.isVisible = false
            b.waiting.isVisible = true
            b.send.isVisible = false
            b.backToTeam.isVisible = true
            b.empty.bind(
                R.drawable.ic_hourglass_top, getString(R.string.register_sent_title, organizer),
                getString(R.string.register_sent_body, team.name, cup.name),
            )
            b.waitingCrest.crest(team.code)
            b.waitingCompetition.text = cup.name
            b.waitingSent.text = getString(R.string.register_sent_on, keepTogether("Sat 15 Nov"))
            b.backToTeam.setOnClickListener { go(TeamActivity::class, TeamVersion.CAPTAIN) }
            return
        }

        b.useYoursCrest.crest(team.code)
        b.useYoursTeam.text = team.name
        b.joinCode.setText(cup.code)
        b.competition.text = cup.name
        b.competitionAbout.text = getString(R.string.register_entering_about, cup.starts, organizer)
        choose(useYours = true)
        b.useYours.setOnClickListener { choose(useYours = true) }
        b.useYoursRadio.setOnClickListener { choose(useYours = true) }
        b.createNew.setOnClickListener { choose(useYours = false) }
        b.createNewRadio.setOnClickListener { choose(useYours = false) }
        b.send.setOnClickListener {
            go(RegisterTeamActivity::class, RegisterTeamVersion.WAITING)
            finish()
        }
    }

    /** One choice is selected at a time: its card gets the blue border and its radio fills. */
    private fun choose(useYours: Boolean) {
        b.useYours.isSelected = useYours
        b.useYoursRadio.isChecked = useYours
        b.createNew.isSelected = !useYours
        b.createNewRadio.isChecked = !useYours
    }
}
