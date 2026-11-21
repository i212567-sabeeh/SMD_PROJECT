package com.leaguex.app

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayout
import com.leaguex.app.screens.Frames
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Every frame on a small phone (720×1280 at 320dpi, 360×640dp) with text at 130%: no text is cut off sideways, no
 * button or chip label is cut short, and nothing runs off the bottom of a screen that doesn't scroll.
 */
@RunWith(AndroidJUnit4::class)
@Config(qualifiers = "w360dp-h640dp-xhdpi")
class SmallPhoneTest {

    @Before
    fun bigText() {
        RuntimeEnvironment.setFontScale(BIG_TEXT)
    }

    @Test
    fun everyFrameFits() {
        val failures = mutableListOf<String>()
        for (frame in Frames.all) {
            val launched = FrameHarness.launch(frame)
            try {
                val windows = listOfNotNull(launched.activity.window.decorView, launched.dialog?.window?.decorView)
                for (root in windows) check(frame.id, root, failures)
            } finally {
                launched.dialog?.dismiss()
                runCatching { launched.controller.pause().stop().destroy() }
                FrameHarness.idle()
            }
        }
        if (failures.isNotEmpty()) fail(failures.joinToString("\n"))
    }

    private fun check(id: String, root: View, failures: MutableList<String>) {
        val window = Rect(0, 0, root.width, root.height)
        for (view in shown(root)) {
            val name = describe(view)
            if (view is TextView && view.layout != null) {
                val layout = view.layout
                val room = view.width - view.totalPaddingLeft - view.totalPaddingRight
                for (line in 0 until layout.lineCount) {
                    val cut = layout.getEllipsisCount(line) == 0 && layout.getLineWidth(line) > room + 1
                    if (cut) failures += "$id: $name is cut off sideways"
                    val shortened = layout.getEllipsisCount(line) > 0 && (view is Button || view is Chip)
                    if (shortened) failures += "$id: $name's label is cut short"
                    if (cut || shortened) break
                }
            }
            // A view drawn see-through (an unselected tab's indicator, say) can't run off anything.
            if (insideScroller(view) || view.alpha == 0f) continue
            val at = IntArray(2).also(view::getLocationInWindow)
            if (at[0] + view.width * view.scaleX > window.right + 1) failures += "$id: $name runs off the right edge"
            if (at[1] + view.height * view.scaleY > window.bottom + 1 && view.height > 0) failures += "$id: $name runs off the bottom"
        }
    }

    /** Views that scroll their content, so it may run past the screen. */
    private fun insideScroller(view: View): Boolean = generateSequence(view.parent as? View) { it.parent as? View }.any {
        it is ScrollView || it is NestedScrollView || it is HorizontalScrollView || it is RecyclerView || it is TabLayout
    }

    private fun shown(view: View): List<View> = if (!view.isShown) {
        emptyList()
    } else {
        listOf(view) + if (view is ViewGroup) (0 until view.childCount).flatMap { shown(view.getChildAt(it)) } else emptyList()
    }

    private fun describe(view: View): String {
        val idName = if (view.id != View.NO_ID) runCatching { view.resources.getResourceEntryName(view.id) }.getOrNull() else null
        val label = (view as? TextView)?.text?.takeIf { it.isNotBlank() }?.toString()?.take(40)
        return listOfNotNull(view.javaClass.simpleName, idName?.let { "#$it" }, label?.let { "\"$it\"" }).joinToString(" ")
    }

    private companion object {
        const val BIG_TEXT = 1.3f
    }
}
