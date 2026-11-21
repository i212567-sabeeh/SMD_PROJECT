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
import org.junit.Assert.assertTrue
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

    /**
     * Opens [intent]'s activity, settled, after forgetting anything started before it. What the activity starts as
     * it opens (a flow's first screen passing the flow on, say) is kept for [next].
     */
    fun open(intent: Intent): Activity {
        drainStarted()
        val cls = Class.forName(intent.component?.className ?: error("the Intent names no activity")).asSubclass(Activity::class.java)
        val activity = Robolectric.buildActivity(cls, intent).setup().get()
        FrameHarness.idle()
        return activity
    }

    fun open(cls: KClass<out Activity>, version: Enum<*>? = null, sheet: String? = null): Activity =
        open(app.screen(cls, version, sheet))

    /**
     * Taps the shown view labelled [label] (its text, or failing that its content description) that can be tapped:
     * the view itself or the nearest tappable view around it, as a finger would. A page title that repeats a
     * button's label is passed over for the button.
     */
    fun Activity.tap(label: String) {
        val wanted = normalize(label)
        val root = sheet()?.window?.decorView ?: window.decorView
        val labelled = shown(root).filter { (it as? TextView)?.text?.let(::normalize) == wanted }
            .ifEmpty { shown(root).filter { it.contentDescription?.let(::normalize) == wanted } }
        if (labelled.isEmpty()) throw AssertionError("${javaClass.simpleName}: nothing shown is labelled \"$label\"")
        val target = labelled.firstNotNullOfOrNull { view ->
            generateSequence(view) { it.parent as? View }.firstOrNull { it.isClickable || it.hasOnClickListeners() }
        } ?: throw AssertionError("${javaClass.simpleName}: \"$label\" can't be tapped")
        if (!target.isEnabled) throw AssertionError("${javaClass.simpleName}: \"$label\" is disabled")
        target.performClick()
        FrameHarness.idle()
    }

    /**
     * The screen the last tap opened, which must be [cls] in [version], opened and settled for the next step. When the
     * tap ended a flow, it first brings back the flow's first screen, which closes the flow's steps and passes on
     * ([Extras.THEN]); that hand-over is followed through to the screen it opens.
     */
    fun next(cls: KClass<out Activity>, version: Enum<*>? = null): Activity {
        var intent: Intent? = shadowOf(app).nextStartedActivity
        while (intent != null && intent.hasExtra(Extras.THEN)) {
            val first = open(intent)
            assertTrue("${first.javaClass.simpleName} closes once it has passed the flow on", first.isFinishing)
            intent = shadowOf(app).nextStartedActivity
        }
        assertNotNull("expected a tap to open ${cls.simpleName}", intent)
        assertEquals(cls.java.name, intent!!.component?.className)
        assertEquals("version of ${cls.simpleName}", version?.name, intent.getStringExtra(Extras.VERSION))
        return open(intent)
    }

    /** The sheet showing over the screen, if there is one. */
    fun sheet(): Dialog? = ShadowDialog.getLatestDialog()?.takeIf { it.isShowing }

    fun drainStarted(): List<Intent> = generateSequence { shadowOf(app).nextStartedActivity }.toList()

    private fun normalize(text: CharSequence) = text.toString().replace('\u00A0', ' ').trim()

    private fun shown(view: View): List<View> = if (!view.isShown) {
        emptyList()
    } else {
        listOf(view) + if (view is ViewGroup) (0 until view.childCount).flatMap { shown(view.getChildAt(it)) } else emptyList()
    }
}
