package com.leaguex.app.organizer

import android.os.Bundle
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.TableRow
import com.leaguex.app.databinding.ActivityStartPlayoffsBinding
import com.leaguex.app.databinding.ItemSemiRowBinding
import com.leaguex.app.kit.crest
import com.leaguex.app.kit.rows.TableAdapter
import com.leaguex.app.kit.rows.showRows

/** 42 Start playoffs: the league season is over; the final table's top 4 make the semi-finals. */
class StartPlayoffsActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityStartPlayoffsBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.footer)
        b.top.title.setText(R.string.playoffs_title)
        backOn(b.top.back)

        b.tableHead.title.setText(R.string.playoffs_final_table)
        b.table.showRows(TableAdapter(FINAL_TABLE, short = true))
        b.semisHead.title.setText(R.string.standings_semis)
        listOf(
            Triple("CAP", "RAW", R.string.playoffs_first_fourth),
            Triple("MAR", "POT", R.string.playoffs_second_third),
        ).forEachIndexed { i, (a, c, why) ->
            val row = ItemSemiRowBinding.inflate(layoutInflater, b.semis, true)
            row.crestA.crest(a)
            row.crestB.crest(c)
            row.label.text = getString(R.string.playoffs_semi, i + 1)
            row.teams.text = "$a vs $c"
            row.why.setText(why)
        }
        b.start.setOnClickListener { finish() }
    }

    private companion object {
        /** The table after matchday 14: the top 4, who go through. */
        val FINAL_TABLE = listOf(
            TableRow(1, "CAP", 14, 0, 0, 0, 21, 34), TableRow(2, "MAR", 14, 0, 0, 0, 17, 31),
            TableRow(3, "POT", 14, 0, 0, 0, 8, 25), TableRow(4, "RAW", 14, 0, 0, 0, 3, 22),
        )
    }
}
