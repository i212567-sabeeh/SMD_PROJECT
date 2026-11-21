package com.leaguex.app

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowDialog
import kotlin.reflect.KClass

/**
 * Walks the app the way a person does: open a screen, tap what it shows by its label, and follow the screen that tap
 * opens. Taps go to the open sheet when there is one, and otherwise to the screen.
 */
object FlowHarness {
    private val app: Application get() = ApplicationProvider.getApplicationContext()

    /** Opens [intent]'s activity, settled, after forgetting anything started before it. */
    fun open(intent: Intent): Activity {
        drainStarted()
        val cls = Class.forName(intent.component?.className ?: error("the Intent names no activity")).asSubclass(Activity::class.java)
        val activity = Robolectric.buildActivity(cls, intent).setup().get()
        FrameHarness.idle()
        drainStarted()
        return activity
    }

    fun open(cls: KClass<out Activity>, version: Enum<*>? = null, sheet: String? = null): Activity =
        open(app.screen(cls, version, sheet))

    /** Taps the shown, enabled view labelled [label]: a text, or failing that a content description. */
    fun Activity.tap(label: String) {
        val wanted = normalize(label)
        val root = sheet()?.window?.decorView ?: window.decorView
        val view = shown(root).firstOrNull { (it as? TextView)?.text?.let(::normalize) == wanted }
            ?: shown(root).firstOrNull { it.contentDescription?.let(::normalize) == wanted }
            ?: throw AssertionError("${javaClass.simpleName}: nothing shown is labelled \"$label\"")
        val target = generateSequence(view) { it.parent as? View }.firstOrNull { it.isClickable || it.hasOnClickListeners() }
            ?: throw AssertionError("${javaClass.simpleName}: \"$label\" can't be tapped")
        if (!target.isEnabled) throw AssertionError("${javaClass.simpleName}: \"$label\" is disabled")
        target.performClick()
        FrameHarness.idle()
    }

    /** The screen the last tap opened, which must be [cls] in [version], opened and settled for the next step. */
    fun next(cls: KClass<out Activity>, version: Enum<*>? = null): Activity {
        val intent = shadowOf(app).nextStartedActivity
        assertNotNull("expected a tap to open ${cls.simpleName}", intent)
        assertEquals(cls.java.name, intent.component?.className)
        assertEquals("version of ${cls.simpleName}", version?.name, intent.getStringExtra(Extras.VERSION))
        return open(intent)
    }

    /** The sheet showing over the screen, if there is one. */
    fun sheet(): Dialog? = ShadowDialog.getLatestDialog()?.takeIf { it.isShowing }

    fun drainStarted(): List<Intent> = generateSequence { shadowOf(app).nextStartedActivity }.toList()

    private fun normalize(text: CharSequence) = text.toString().replace(' ', ' ').trim()

    private fun shown(view: View): List<View> = if (!view.isShown) {
        emptyList()
    } else {
        listOf(view) + if (view is ViewGroup) (0 until view.childCount).flatMap { shown(view.getChildAt(it)) } else emptyList()
    }
}
