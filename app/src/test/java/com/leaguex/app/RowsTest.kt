package com.leaguex.app

import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.leaguex.app.data.EventType
import com.leaguex.app.data.SampleData
import com.leaguex.app.kit.StatusKind
import com.leaguex.app.kit.rows.ActionRow
import com.leaguex.app.kit.rows.ActionRowAdapter
import com.leaguex.app.kit.rows.EventAdapter
import com.leaguex.app.kit.rows.MatchRowAdapter
import com.leaguex.app.kit.rows.NoteRow
import com.leaguex.app.kit.rows.NoteRowAdapter
import com.leaguex.app.kit.rows.PlayerRow
import com.leaguex.app.kit.rows.PlayerRowAdapter
import com.leaguex.app.kit.rows.ResultRowAdapter
import com.leaguex.app.kit.rows.SyncItem
import com.leaguex.app.kit.rows.SyncItemAdapter
import com.leaguex.app.kit.rows.TableAdapter
import com.leaguex.app.kit.rows.TeamRow
import com.leaguex.app.kit.rows.TeamRowAdapter
import com.leaguex.app.kit.rows.showRows
import com.leaguex.app.screens.StyleKitActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config

/** The sample data is data.js's, and every row adapter shows it the way ui.js's row functions do. */
@RunWith(AndroidJUnit4::class)
@Config(qualifiers = "w412dp-h917dp-xxhdpi")
class RowsTest {

    @Test
    fun sampleDataMatchesFigma() {
        val table = SampleData.TABLE
        assertEquals(8, table.size)
        assertEquals("CAP", table.first().code)
        assertEquals(12, table.first().pts)
        assertEquals("KOH", table.last().code)
        assertEquals(-8, table.last().gd)

        val mar = SampleData.SQUADS.getValue("MAR")
        assertEquals(9, mar.size)
        assertEquals("Hassan Javed", mar.last().name)

        assertEquals(8, SampleData.MAR_RAW.size)
        assertEquals(EventType.PERIOD, SampleData.MAR_RAW[2].type)
        assertEquals("Half time, 1–1", SampleData.MAR_RAW[2].player)

        assertEquals(listOf("12'", "25'", "33'", "38'"), SampleData.CUP_SEMI.filter { it.type == EventType.GOAL }.map { it.minute })
    }

    @Test
    fun rowsShowTheirData() {
        val mar = SampleData.SQUADS.getValue("MAR")
        val cases = listOf(
            MatchRowAdapter(SampleData.MATCHDAY5) to listOf("MAR", "Margalla Strikers", "7:00 pm", "Court 2"),
            ResultRowAdapter(SampleData.MATCHDAY4) to listOf("CAP", "3–1", "FT"),
            TableAdapter(SampleData.TABLE, highlight = "MAR") to listOf("Team", "Pts", "Capital United", "+7", "−8"),
            PlayerRowAdapter(mar.map { PlayerRow(it.name, it.position, number = it.number) }) to listOf("Hassan Javed", "Goalkeeper", "12"),
            TeamRowAdapter(listOf(TeamRow("SAD", "Captain: Rehan Butt"))) to listOf("Saddar City", "Captain: Rehan Butt"),
            NoteRowAdapter(listOf(NoteRow(R.drawable.ic_sports_soccer, "Goal: Zain Abbas scores for Margalla Strikers, 1–0", time = "7:06 pm", unread = true)))
                to listOf("Goal: Zain Abbas scores for Margalla Strikers, 1–0", "7:06 pm"),
            EventAdapter(SampleData.MAR_RAW) to listOf("6'", "Zain Abbas", "Assist: Usman Tariq", "Half time, 1–1", "Daniyal Rauf on"),
            SyncItemAdapter(listOf(SyncItem(R.drawable.ic_sports_soccer, "Goal, Usman Tariq, 29'", "MAR vs RAW, saved 7:46 pm", StatusKind.WAITING, "Waiting")))
                to listOf("Goal, Usman Tariq, 29'", "MAR vs RAW, saved 7:46 pm", "Waiting"),
        )
        for ((adapter, expected) in cases) {
            val text = shown(adapter)
            for (e in expected) assertTrue("${adapter.javaClass.simpleName}: missing \"$e\" in \"$text\"", FrameHarness.normalize(e) in text)
        }
    }

    /** Names and titles in rows stay on one line and end in "…" (Review Focus 5). */
    @Test
    fun rowTitlesNeverWrap() {
        val host = Robolectric.buildActivity(StyleKitActivity::class.java).setup().get()
        val parent = FrameLayout(host)
        for ((layout, ids) in NAME_VIEWS) {
            val row = host.layoutInflater.inflate(layout, parent, false)
            for (id in ids) {
                val view = row.findViewById<TextView>(id)
                val name = host.resources.getResourceEntryName(layout) + "#" + host.resources.getResourceEntryName(id)
                assertEquals("$name maxLines", 1, view.maxLines)
                assertEquals("$name ellipsize", TextUtils.TruncateAt.END, view.ellipsize)
            }
        }
    }

    /** The dashed playoff line follows only the row at playoffAfter (15 Table: after 4th place). */
    @Test
    fun playoffLineFollowsItsRow() {
        val list = laidOut(TableAdapter(SampleData.TABLE, playoffAfter = 4))
        val shown = (1 until list.childCount).map { list.getChildAt(it).findViewById<View>(R.id.playoffLine).isShown }
        assertEquals(listOf(false, false, false, true, false, false, false, false), shown)
    }

    /** A trailing lambda is the row's tap, for action rows as for player rows (Search's league, Profile's lists). */
    @Test
    fun trailingLambdaTapsTheRow() {
        val row = ActionRow(R.drawable.ic_emoji_events, "Islamabad Futsal League", "League, 8 teams")
        val tapped = mutableListOf<ActionRow>()
        val list = laidOut(ActionRowAdapter(listOf(row)) { tapped += it })
        list.getChildAt(0).performClick()
        assertEquals(listOf(row), tapped)
    }

    /** What [adapter]'s rows show in a 412dp-wide list on a LeagueX-themed screen. */
    private fun shown(adapter: RecyclerView.Adapter<*>): String = FrameHarness.textOf(laidOut(adapter))

    private fun laidOut(adapter: RecyclerView.Adapter<*>): RecyclerView {
        val host = Robolectric.buildActivity(StyleKitActivity::class.java).setup().get()
        val list = RecyclerView(host)
        host.setContentView(FrameLayout(host).apply { addView(list, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)) })
        list.showRows(adapter)
        FrameHarness.idle()
        list.measure(View.MeasureSpec.makeMeasureSpec(host.resources.displayMetrics.widthPixels, View.MeasureSpec.EXACTLY), View.MeasureSpec.UNSPECIFIED)
        list.layout(0, 0, list.measuredWidth, list.measuredHeight)
        return list
    }

    private companion object {
        val NAME_VIEWS = listOf(
            R.layout.item_match_row to listOf(R.id.homeName, R.id.awayName),
            R.layout.item_table_row to listOf(R.id.teamName),
            R.layout.item_player_row to listOf(R.id.name),
            R.layout.item_team_row to listOf(R.id.name),
            R.layout.item_event to listOf(R.id.player),
            R.layout.item_sync to listOf(R.id.title),
        )
    }
}
