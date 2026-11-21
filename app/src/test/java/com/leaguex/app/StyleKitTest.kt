package com.leaguex.app

import android.app.Activity
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.leaguex.app.databinding.IncludeBottomNavBinding
import com.leaguex.app.databinding.SheetStyleKitBinding
import com.leaguex.app.kit.StatusKind
import com.leaguex.app.kit.avatar
import com.leaguex.app.kit.crest
import com.leaguex.app.kit.showSheet
import com.leaguex.app.kit.status
import com.leaguex.app.screens.StyleKitActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowDialog
import kotlin.math.roundToInt

/** The Style kit screen mirrors the Figma's Style kit page, and its parts behave as the kit describes. */
@RunWith(AndroidJUnit4::class)
@Config(qualifiers = "w412dp-h917dp-xxhdpi")
class StyleKitTest {

    @Test
    fun showsTheStyleGuidesSectionsCaptionsAndSamples() {
        val texts = shownTexts(launch().window.decorView)
        val missing = (SECTIONS + CAPTIONS + SAMPLES).filter { it !in texts }
        assertTrue("missing $missing", missing.isEmpty())
        val order = SECTIONS.map { texts.indexOf(it) }
        assertEquals("sections out of the style guide's order", order.sorted(), order)
    }

    /** A text appearance set in a layout keeps its font: the theme mustn't set one that overrides it. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun textAppearancesInLayoutsKeepTheirFonts() {
        val kit = launch()
        val shown = shownTextViews(kit.window.decorView)
        fun assertFont(what: String, font: Int, text: String) {
            val view = shown.first { it.text.toString() == text }
            val expected = Paint(view.paint).apply { typeface = ResourcesCompat.getFont(kit, font) }.measureText(text)
            assertEquals(what, expected, view.paint.measureText(text), 0.5f)
        }
        assertFont("Display/Title is Anton", R.font.anton_regular, "Style guide: Matchday")
        assertFont("Title/Large is Archivo SemiBold", R.font.archivo_semibold, "Colours")
        assertFont("Label/Medium is Archivo Medium", R.font.archivo_medium, "Filled")
        assertFont("a filled button is Archivo SemiBold", R.font.archivo_semibold, "Record match")
    }

    @Test
    fun bottomSheetCellOpensTheRealSheet() {
        val kit = launch()
        kit.findViewById<View>(R.id.sheetCell).performClick()
        FrameHarness.idle()
        val sheet = ShadowDialog.getLatestDialog() as BottomSheetDialog
        assertTrue("the sheet isn't showing", sheet.isShowing)
        assertEquals("the sheet isn't expanded", BottomSheetBehavior.STATE_EXPANDED, sheet.behavior.state)
        assertTrue("Profile photo" in shownTexts(sheet.window!!.decorView))

        sheet.findViewById<View>(R.id.sheetClose)!!.performClick()
        FrameHarness.idle()
        assertFalse("close didn't dismiss the sheet", sheet.isShowing)
    }

    @Test
    fun sheetDimsWithTheScrimAndKeepsTheStatusIconsOfTheScreenUnderIt() {
        val kit = launch()
        for (onBlue in listOf(false, true)) {
            WindowCompat.getInsetsController(kit.window, kit.window.decorView).isAppearanceLightStatusBars = !onBlue
            val sheet = kit.showSheet(SheetStyleKitBinding.inflate(kit.layoutInflater).root)
            FrameHarness.idle()
            val window = sheet.window!!
            val lightIcons = !WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars
            assertEquals("status icons over a ${if (onBlue) "blue" else "light"} top", onBlue, lightIcons)
            assertTrue("no dim behind the sheet", window.attributes.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND != 0)
            val scrimAlpha = Color.alpha(ContextCompat.getColor(kit, R.color.scrim)) / 255f
            assertEquals(scrimAlpha, window.attributes.dimAmount, 0.001f)
            sheet.dismiss()
        }
    }

    @Test
    fun bottomBarIs80dpTallPlusTheNavigationBar() {
        val kit = launch()
        val nav = IncludeBottomNavBinding.inflate(kit.layoutInflater).root
        val inset = px(kit, 24)
        val insets = WindowInsetsCompat.Builder()
            .setInsets(WindowInsetsCompat.Type.navigationBars(), Insets.of(0, 0, 0, inset))
            .build()
        ViewCompat.dispatchApplyWindowInsets(nav, insets)
        nav.measure(View.MeasureSpec.makeMeasureSpec(px(kit, 412), View.MeasureSpec.EXACTLY), View.MeasureSpec.UNSPECIFIED)
        assertEquals(px(kit, 80) + inset, nav.measuredHeight)
    }

    @Test
    fun bindersDrawWhatTheKitDraws() {
        val kit = launch()
        fun colour(id: Int) = ContextCompat.getColor(kit, id)

        val pot = TextView(kit).apply { crest("POT") }
        assertEquals("POT", pot.text.toString())
        assertEquals("POT uses ink text", colour(R.color.ink), pot.currentTextColor)
        assertEquals(colour(R.color.team_pot), pot.backgroundTintList?.defaultColor)
        val mar = TextView(kit).apply { crest("MAR") }
        assertEquals(colour(R.color.on_blue), mar.currentTextColor)
        assertEquals(colour(R.color.team_mar), mar.backgroundTintList?.defaultColor)

        assertEquals("UT", TextView(kit).apply { avatar("Usman Tariq") }.text.toString())
        assertEquals("MA", TextView(kit).apply { avatar("Muhammad Abdullah Qureshi") }.text.toString())

        // status() in code gives each kind the look of its Widget.LeagueX.Status.<Kind> style.
        for (kind in StatusKind.entries) {
            val styled = TextView(kit, null, 0, kind.style)
            val bound = TextView(kit).apply { status(kind, "Synced") }
            assertEquals("$kind text", styled.currentTextColor, bound.currentTextColor)
            assertEquals("$kind background", styled.backgroundTintList?.defaultColor, bound.backgroundTintList?.defaultColor)
            assertNotNull("$kind has no background", bound.background)
            assertEquals("$kind icon", styled.compoundDrawablesRelative[0] != null, bound.compoundDrawablesRelative[0] != null)
            assertEquals("$kind height", styled.minHeight, bound.minHeight)
            assertEquals("$kind padding", styled.paddingStart, bound.paddingStart)
            assertEquals("Synced", bound.text.toString())
        }
    }

    private fun launch(): StyleKitActivity =
        Robolectric.buildActivity(StyleKitActivity::class.java).setup().get().also { FrameHarness.idle() }

    private fun px(activity: Activity, dp: Int) = (dp * activity.resources.displayMetrics.density).roundToInt()

    /** Every shown TextView, in tree order. */
    private fun shownTextViews(view: View): List<TextView> = buildList {
        fun walk(v: View) {
            if (!v.isShown) return
            if (v is TextView) add(v)
            if (v is ViewGroup) for (i in 0 until v.childCount) walk(v.getChildAt(i))
        }
        walk(view)
    }

    /** The text of every shown TextView, in tree order, whitespace collapsed. */
    private fun shownTexts(view: View): List<String> = shownTextViews(view).map { FrameHarness.normalize(it.text.toString()) }

    private companion object {
        val SECTIONS = listOf("Colours", "Type", "Buttons and inputs", "Identity, lists and rows", "Match parts", "Navigation and system")
        val CAPTIONS = listOf(
            "Filled", "Tonal", "Outlined", "Text", "Danger", "On blue", "Icon buttons", "Filter chips", "Status chips",
            "Text fields", "Search", "Dropdown", "Toggle, checkbox, radio", "Tabs and steps", "Crests", "Avatars",
            "Match rows", "Table rows, full width as on Standings", "Player and team rows", "Settings and notifications",
            "Scoreboard", "Match clock", "Timeline", "Bracket ties", "Top bars", "Bottom nav", "Bottom sheet",
            "Offline banner and snackbar", "Empty state", "League banner", "Push notification",
        )
        val SAMPLES = listOf("Record match", "Waiting to upload", "MAR win 4–3 on penalties", "20+1", "Step 2 of 4: Details")
    }
}
