package com.leaguex.app.news

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ConcatAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivitySearchBinding
import com.leaguex.app.databinding.SheetFiltersBinding
import com.leaguex.app.kit.bind
import com.leaguex.app.kit.rows.ActionRow
import com.leaguex.app.kit.rows.ActionRowAdapter
import com.leaguex.app.kit.rows.PlayerRow
import com.leaguex.app.kit.rows.PlayerRowAdapter
import com.leaguex.app.kit.rows.TeamRow
import com.leaguex.app.kit.rows.TeamRowAdapter
import com.leaguex.app.kit.rows.showRows
import com.leaguex.app.kit.showSheet
import com.leaguex.app.league.LeagueActivity

/** 10 Search's versions: the Figma's search for forwards, and 57 Empty / No search results for a misspelling. */
enum class SearchVersion { DEFAULT, NO_RESULTS }

/** 10 Search: leagues, teams and players, narrowed by tab and filters. The filters open 11 Filters as a sheet. */
class SearchActivity : LeagueXActivity() {

    private lateinit var b: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.content)
        backOn(b.back)
        b.tabs.getTabAt(PLAYERS)?.select()
        // The Figma always shows the clear button; Material's own clear-text icon only shows while the box has focus.
        b.searchBox.endIconMode = TextInputLayout.END_ICON_CUSTOM
        b.searchBox.setEndIconDrawable(R.drawable.ic_close)
        b.searchBox.endIconContentDescription = getString(R.string.kit_cd_close)
        b.searchBox.setEndIconOnClickListener { b.query.text = null }
        b.city.text = SampleData.ME.city
        b.empty.bind(
            R.drawable.ic_search_off, getString(R.string.search_no_results, MISSPELLED),
            getString(R.string.search_no_results_body), getString(R.string.search_clear_filters),
        )
        b.empty.button.setOnClickListener {
            go(SearchActivity::class)
            finish()
        }

        if (versionOr(SearchVersion.DEFAULT) == SearchVersion.NO_RESULTS) {
            b.query.setText(MISSPELLED)
            b.results.isVisible = false
            b.emptyArea.isVisible = true
            return
        }

        b.query.setText(getString(R.string.position_forward))
        b.moreFilters.setOnClickListener { filters() }
        b.city.setOnClickListener { filters() }
        b.city.setOnCloseIconClickListener { filters() }
        b.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) = results(tab.position)
            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })
        results(PLAYERS)
        if (sheetToOpen == SHEET_FILTERS) filters()
    }

    /** The results for [tab]: All, Leagues, Teams or Players. */
    private fun results(tab: Int) {
        val forwards = listOf("Zain Abbas" to "MAR", "Nabeel Haider" to "RAW", "Muhammad Abdullah Qureshi" to "MAR")
        val players = PlayerRowAdapter(
            forwards.map { (name, team) -> PlayerRow(name, getString(R.string.search_player_meta, getString(R.string.position_forward), SampleData.teamName(team))) },
        ) { go(PlayerActivity::class) }
        val teams = TeamRowAdapter(listOf("MAR", "RAW").map { TeamRow(it, getString(R.string.teams_meta_short, SampleData.TEAMS.getValue(it).captain)) }) {
            go(TeamActivity::class)
        }
        val (name, about) = SampleData.SUGGESTIONS.first()
        val leagues = ActionRowAdapter(listOf(ActionRow(R.drawable.ic_emoji_events, name, about))) { go(LeagueActivity::class) }
        when (tab) {
            ALL -> {
                b.count.text = getString(R.string.search_results_count, 6)
                b.list.showRows(ConcatAdapter(leagues, teams, players))
            }
            LEAGUES -> {
                b.count.text = getString(R.string.search_leagues_count, 1)
                b.list.showRows(leagues)
            }
            TEAMS -> {
                b.count.text = getString(R.string.search_teams_count, 2)
                b.list.showRows(teams)
            }
            else -> {
                b.count.text = getString(R.string.search_players_count, forwards.size)
                b.list.showRows(players)
            }
        }
    }

    /** 11 Filters: where, which positions and which kinds of competition. */
    private fun filters() {
        val sheet = SheetFiltersBinding.inflate(layoutInflater)
        sheet.head.sheetTitle.setText(R.string.filters_title)
        sheet.province.setText(SampleData.ME.province, false)
        sheet.city.setText(SampleData.ME.city, false)
        val dialog = showSheet(sheet.root)
        sheet.clear.setOnClickListener {
            listOf(sheet.positions, sheet.kinds).forEach { group ->
                for (i in 0 until group.childCount) (group.getChildAt(i) as? Chip)?.isChecked = false
            }
        }
        sheet.show.setOnClickListener { dialog.dismiss() }
    }

    companion object {
        /** Extras.SHEET value that opens 11 Filters over Search. */
        const val SHEET_FILTERS = "filters"

        private const val ALL = 0
        private const val LEAGUES = 1
        private const val TEAMS = 2
        private const val PLAYERS = 3

        /** 57 Empty / No search results' query. */
        private const val MISSPELLED = "Forwad"
    }
}
