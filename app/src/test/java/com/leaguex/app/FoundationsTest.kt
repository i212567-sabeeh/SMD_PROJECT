package com.leaguex.app

import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(qualifiers = "w412dp-h917dp-xxhdpi")
class FoundationsTest {
    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    private fun resource(name: String): String =
        checkNotNull(javaClass.classLoader!!.getResourceAsStream(name)) { "missing test resource $name" }
            .bufferedReader().readText()

    private fun id(type: String, name: String): Int = context.resources.getIdentifier(name, type, context.packageName)

    @Test
    fun coloursMatchFigma() {
        val tokens = JSONObject(resource("figma-tokens.json"))
        assertTrue(tokens.length() > 0)
        for (name in tokens.keys()) {
            val res = id("color", name)
            assertTrue("$name: no @color/$name", res != 0)
            assertEquals(name, Color.parseColor(tokens.getString(name)), ContextCompat.getColor(context, res))
        }
    }

    @Test
    fun fontsLoad() {
        for (name in listOf("anton_regular", "archivo_regular", "archivo_medium", "archivo_semibold", "archivo_bold")) {
            val res = id("font", name)
            assertTrue("$name: no @font/$name", res != 0)
            assertNotNull(name, ResourcesCompat.getFont(context, res))
        }
    }

    @Test
    fun typeScaleExists() {
        val styles = listOf(
            R.style.TextAppearance_LeagueX_Display_Wordmark, R.style.TextAppearance_LeagueX_Display_Hero,
            R.style.TextAppearance_LeagueX_Display_Clock, R.style.TextAppearance_LeagueX_Display_Score,
            R.style.TextAppearance_LeagueX_Display_Title, R.style.TextAppearance_LeagueX_Display_Small,
            R.style.TextAppearance_LeagueX_Title_Large, R.style.TextAppearance_LeagueX_Title_Medium,
            R.style.TextAppearance_LeagueX_Title_Small, R.style.TextAppearance_LeagueX_Body_Large,
            R.style.TextAppearance_LeagueX_Body_Medium, R.style.TextAppearance_LeagueX_Body_Small,
            R.style.TextAppearance_LeagueX_Label_Large, R.style.TextAppearance_LeagueX_Label_Medium,
            R.style.TextAppearance_LeagueX_Label_Bold,
        )
        assertEquals(15, styles.toSet().size)
        for (style in styles) {
            val a = context.obtainStyledAttributes(style, intArrayOf(android.R.attr.textSize))
            assertTrue(context.resources.getResourceEntryName(style), a.getDimension(0, 0f) > 0f)
            a.recycle()
        }
    }

    @Test
    fun everyIconExists() {
        val icons = resource("icons.txt").lines().map { it.trim() }.filter { it.isNotEmpty() }
        assertTrue(icons.isNotEmpty())
        for (n in icons) assertTrue("ic_$n", id("drawable", "ic_$n") != 0)
        for (n in listOf("home", "sports_soccer", "leaderboard", "groups", "person")) {
            assertTrue("ic_${n}_fill", id("drawable", "ic_${n}_fill") != 0)
        }
    }

    @Test
    fun themeLeavesTextColourToAppearances() {
        val theme = context.resources.newTheme().apply { applyStyle(R.style.Theme_LeagueX, true) }
        val value = android.util.TypedValue()
        val direct = theme.resolveAttribute(android.R.attr.textColor, value, true)
        assertTrue("theme must not set android:textColor, it overrides every text appearance", !direct)
        assertTrue(theme.resolveAttribute(android.R.attr.textColorPrimary, value, true))
        assertEquals(ContextCompat.getColor(context, R.color.ink), value.data)
        assertTrue(theme.resolveAttribute(android.R.attr.textColorSecondary, value, true))
        assertEquals(ContextCompat.getColor(context, R.color.muted), value.data)
    }
}
