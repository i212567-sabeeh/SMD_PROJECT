package com.leaguex.app

import android.app.Application
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.core.view.WindowCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.leaguex.app.screens.Frame
import com.leaguex.app.screens.Frames
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

/**
 * Checks every frame of one Figma section against the HTML build's manifest. Each failure line starts with
 * the frame id, for example `s08-home-league: missing "Matchday 5 kick-off times"`.
 */
@RunWith(AndroidJUnit4::class)
@Config(qualifiers = "w412dp-h917dp-xxhdpi")
abstract class FramesTestBase(private val slug: String) {

    private val app: Application get() = ApplicationProvider.getApplicationContext()

    /** Every frame in the section is registered, opens, and shows every `expect` string. */
    @Test
    fun showsFigmaText() = eachFrame { figma, frame, failures ->
        if (frame.name != figma.name) failures += "${figma.id}: named \"${frame.name}\" in Frames, \"${figma.name}\" in the Figma"
        if (Frames.sections.firstOrNull { it.slug == slug }?.frames.orEmpty().none { it.id == figma.id }) {
            failures += "${figma.id}: registered outside the $slug section"
        }
        val text = FrameHarness.visibleText(FrameHarness.launch(frame))
        for (expected in figma.expect) {
            if (FrameHarness.normalize(expected) !in text) failures += "${figma.id}: missing \"$expected\""
        }
    }

    /** Status-bar icons are light exactly when the Figma's top is blue, and the bottom bar shows exactly when the Figma draws it. */
    @Test
    fun topAndNavMatchFigma() = eachFrame { figma, frame, failures ->
        val l = FrameHarness.launch(frame)
        val window = l.activity.window
        val lightIcons = !WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars
        if (lightIcons != (figma.top == "blue")) {
            failures += "${figma.id}: status icons are ${if (lightIcons) "light" else "dark"}, but the Figma's top is ${figma.top}"
        }
        // A sheet covers the bottom of the screen, so the bar only counts as shown when no dialog is open.
        val navShown = l.dialog == null && FrameHarness.shownViews(l).any { it is BottomNavigationView }
        if (navShown != figma.nav) {
            failures += "${figma.id}: the bottom bar is ${if (navShown) "shown" else "not shown"}, but the Figma ${if (figma.nav) "draws" else "doesn't draw"} it"
        }
    }

    /**
     * Opens each frame once and taps every shown, enabled, clickable view (and the open PopupMenu's items).
     * Nothing may throw, and every activity a tap starts must be declared in the merged manifest.
     */
    @Test
    fun everyTapIsSafe() = eachFrame { figma, frame, failures ->
        drainStarted()
        val l = FrameHarness.launch(frame)
        val clickable = FrameHarness.shownViews(l).filter { it.isClickable || it.hasOnClickListeners() }
        l.popup?.let { popup ->
            val listener = shadowOf(popup).onMenuItemClickListener
            for (i in 0 until popup.menu.size()) {
                val item = popup.menu.getItem(i)
                if (!item.isVisible || !item.isEnabled) continue
                tap(figma, "menu item \"${item.title}\"", failures) { listener?.onMenuItemClick(item) }
            }
        }
        for (view in clickable) {
            if (!view.isShown || !view.isEnabled) continue
            tap(figma, view.describe(), failures) { view.performClick() }
        }
        val resolver = app.packageManager
        for (intent in drainStarted()) {
            if (resolver.resolveActivity(intent, 0) == null) {
                failures += "${figma.id}: a tap starts ${intent.component?.className ?: intent.toString()}, which the manifest doesn't declare"
            }
        }
    }

    private fun eachFrame(check: (FigmaFrame, Frame, MutableList<String>) -> Unit) {
        val section = FigmaManifest.sections.firstOrNull { it.slug == slug }
            ?: return fail("$slug: not a section of figma-frames.json")
        val failures = mutableListOf<String>()
        for (figma in section.frames) {
            val frame = Frames.byId(figma.id)
            if (frame == null) {
                failures += "${figma.id}: not in Frames"
                continue
            }
            try {
                check(figma, frame, failures)
            } catch (e: Throwable) {
                failures += "${figma.id}: ${e.describe()}"
            }
        }
        if (failures.isNotEmpty()) fail(failures.joinToString("\n"))
    }

    private fun tap(figma: FigmaFrame, what: String, failures: MutableList<String>, click: () -> Unit) {
        try {
            click()
            FrameHarness.idle()
        } catch (e: Throwable) {
            failures += "${figma.id}: tapping $what threw ${e.describe()}"
        }
    }

    private fun drainStarted(): List<Intent> {
        val shadow = shadowOf(app)
        return generateSequence { shadow.nextStartedActivity }.toList()
    }

    private fun View.describe(): String {
        val idName = if (id != View.NO_ID) runCatching { resources.getResourceEntryName(id) }.getOrNull() else null
        val label = (this as? TextView)?.text?.takeIf { it.isNotBlank() }
        return listOfNotNull(javaClass.simpleName, idName?.let { "#$it" }, label?.let { "\"$it\"" }).joinToString(" ")
    }

    private fun Throwable.describe(): String = "${javaClass.simpleName}: $message"
}
