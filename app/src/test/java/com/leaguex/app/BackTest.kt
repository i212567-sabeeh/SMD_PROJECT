package com.leaguex.app

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.activity.ComponentDialog
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.leaguex.app.FlowHarness.drainStarted
import com.leaguex.app.FlowHarness.open
import com.leaguex.app.FlowHarness.sheet
import com.leaguex.app.home.HomeActivity
import com.leaguex.app.league.MatchActivity
import com.leaguex.app.news.SearchActivity
import com.leaguex.app.news.TeamsActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf

/** What the system back does: tabs fall back to Home, other screens close, and an open sheet closes first. */
@RunWith(AndroidJUnit4::class)
class BackTest {

    private val app: Application get() = ApplicationProvider.getApplicationContext()

    @Test
    fun backFromATabGoesHome() {
        val teams = open(TeamsActivity::class) as ComponentActivity
        teams.onBackPressedDispatcher.onBackPressed()
        FrameHarness.idle()
        assertEquals(HomeActivity::class.java.name, shadowOf(app).nextStartedActivity?.component?.className)
        assertTrue(teams.isFinishing)
    }

    @Test
    fun backFromTheMatchPageClosesIt() {
        val match = open(MatchActivity::class) as ComponentActivity
        match.onBackPressedDispatcher.onBackPressed()
        FrameHarness.idle()
        assertEquals(emptyList<Any>(), drainStarted())
        assertTrue(match.isFinishing)
    }

    @Test
    fun backClosesTheFiltersSheetFirst() {
        val search = open(SearchActivity::class, sheet = SearchActivity.SHEET_FILTERS)
        val filters = sheet() as ComponentDialog
        filters.onBackPressedDispatcher.onBackPressed()
        FrameHarness.idle()
        assertNull("the sheet closes", sheet())
        assertFalse("Search stays", search.isFinishing)
    }
}
