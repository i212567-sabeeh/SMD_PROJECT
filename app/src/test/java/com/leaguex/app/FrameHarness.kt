package com.leaguex.app

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.google.android.material.textfield.TextInputLayout
import com.leaguex.app.screens.Frame
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController
import org.robolectric.shadows.ShadowDialog
import org.robolectric.shadows.ShadowPopupMenu

/** A frame's activity, opened by [FrameHarness.launch]. */
class Launched(
    val frame: Frame,
    val controller: ActivityController<out Activity>,
    private val dialogBefore: Dialog?,
    private val popupBefore: PopupMenu?,
) {
    val activity: Activity get() = controller.get()

    /** The newest dialog (a sheet, say) shown since the launch, while it's still showing. */
    val dialog: Dialog? get() = ShadowDialog.getLatestDialog()?.takeIf { it !== dialogBefore && it.isShowing }

    /** The newest android.widget.PopupMenu shown since the launch, while it's still showing. */
    val popup: PopupMenu? get() = ShadowPopupMenu.getLatestPopupMenu()?.takeIf { it !== popupBefore && shadowOf(it).isShowing }
}

object FrameHarness {
    private val WHITESPACE = Regex("[\\s\\u00A0]+")

    /** Opens the frame's Intent the way the screen list would, and lets the main looper settle. */
    fun launch(frame: Frame): Launched {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val intent = frame.open(app)
        val component = intent.component ?: error("its Intent names no activity")
        val cls = Class.forName(component.className).asSubclass(Activity::class.java)
        val dialogBefore = ShadowDialog.getLatestDialog()
        val popupBefore = ShadowPopupMenu.getLatestPopupMenu()
        val controller = Robolectric.buildActivity(cls, intent).setup()
        idle()
        return Launched(frame, controller, dialogBefore, popupBefore)
    }

    fun idle() = shadowOf(Looper.getMainLooper()).idle()

    /**
     * Everything the frame shows as text: shown TextViews' text and hints, TextInputLayout hints and helper
     * text, the open dialog's window and the open PopupMenu's item titles. Whitespace (including U+00A0) is
     * collapsed to single spaces, as the HTML checker does with innerText.
     */
    fun visibleText(l: Launched): String {
        val parts = mutableListOf<CharSequence>()
        windows(l).forEach { collectText(it, parts) }
        l.popup?.menu?.let { menu ->
            for (i in 0 until menu.size()) menu.getItem(i).takeIf { it.isVisible }?.title?.let(parts::add)
        }
        return normalize(parts.joinToString(" "))
    }

    /** The text [view] and its shown children show, collapsed as in [visibleText]. */
    fun textOf(view: View): String = normalize(mutableListOf<CharSequence>().also { collectText(view, it) }.joinToString(" "))

    /** Every shown view of the activity's window and of the open dialog's window, in tree order. */
    fun shownViews(l: Launched): List<View> = mutableListOf<View>().also { out -> windows(l).forEach { collectShown(it, out) } }

    fun normalize(text: String): String = text.replace(WHITESPACE, " ").trim()

    private fun windows(l: Launched): List<View> = listOfNotNull(l.activity.window.decorView, l.dialog?.window?.decorView)

    private fun collectShown(view: View, out: MutableList<View>) {
        if (!view.isShown) return
        out += view
        if (view is ViewGroup) for (i in 0 until view.childCount) collectShown(view.getChildAt(i), out)
    }

    private fun collectText(view: View, out: MutableList<CharSequence>) {
        if (!view.isShown) return
        when (view) {
            is TextInputLayout -> listOfNotNull(view.hint, view.helperText).forEach(out::add)
            is TextView -> listOfNotNull(view.text, view.hint).forEach(out::add)
        }
        if (view is ViewGroup) for (i in 0 until view.childCount) collectText(view.getChildAt(i), out)
    }
}
