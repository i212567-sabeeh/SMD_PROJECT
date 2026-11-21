package com.leaguex.app.home

import android.app.Activity
import android.view.View
import android.widget.RadioButton
import com.leaguex.app.R
import com.leaguex.app.data.Competition
import com.leaguex.app.data.SampleData
import com.leaguex.app.data.Session
import com.leaguex.app.databinding.ItemSheetOptionBinding
import com.leaguex.app.databinding.SheetSwitchLeagueBinding
import com.leaguex.app.kit.showSheet
import com.leaguex.app.news.SearchActivity
import com.leaguex.app.screen

/**
 * 09 Switch league, opened from the competition's name at the top of Home and the tab screens: the competitions you
 * play in or follow. Picking the league or the cup makes it the competition the app shows ([Session]) and runs
 * [onPick] with it. Find more opens Search.
 */
fun Activity.showSwitchLeague(onPick: (Competition) -> Unit) {
    val sheet = SheetSwitchLeagueBinding.inflate(layoutInflater)
    sheet.head.sheetTitle.setText(R.string.league_switch_title)
    val dialog = showSheet(sheet.root)
    val mar = SampleData.teamName("MAR")
    val options = listOf(
        Triple(SampleData.LEAGUE.name, getString(R.string.league_you_play_for, getString(R.string.league_kind_league), mar), SampleData.LEAGUE),
        Triple(SampleData.CUP.name, getString(R.string.league_you_play_for, getString(R.string.league_kind_knockout), mar), SampleData.CUP),
        Triple(SampleData.FOLLOWED_LEAGUE, getString(R.string.league_following, getString(R.string.league_kind_league)), null),
    )
    sheet.findMore.setOnClickListener {
        dialog.dismiss()
        startActivity(screen(SearchActivity::class))
    }
    val chosen = if (Session.cupPicked) 1 else 0
    val radios = mutableListOf<RadioButton>()
    options.forEachIndexed { i, (name, meta, competition) ->
        val row = ItemSheetOptionBinding.inflate(layoutInflater, sheet.options, true)
        row.name.text = name
        row.meta.text = meta
        row.radio.isChecked = i == chosen
        radios += row.radio
        val pick = View.OnClickListener {
            radios.forEachIndexed { j, radio -> radio.isChecked = j == i }
            if (competition != null && competition != Session.competition) {
                dialog.dismiss()
                Session.competition = competition
                onPick(competition)
            }
        }
        row.root.setOnClickListener(pick)
        row.radio.setOnClickListener(pick)
    }
}

/** Home's version for [competition]: the cup's Home, or the league's. */
fun homeVersionFor(competition: Competition): HomeVersion =
    if (competition == SampleData.CUP) HomeVersion.TOURNAMENT else HomeVersion.LEAGUE
