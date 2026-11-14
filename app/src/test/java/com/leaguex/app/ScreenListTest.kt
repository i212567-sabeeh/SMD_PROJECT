package com.leaguex.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.leaguex.app.screens.Frames
import com.leaguex.app.screens.ScreenListActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(qualifiers = "w412dp-h917dp-xxhdpi")
class ScreenListTest {

    @Test
    fun listsStartSectionsAndKit() {
        val rows = rowTexts(launchList())
        val titles = FigmaManifest.sections.map { it.title }
        for (wanted in listOf(START) + titles + KIT) assertTrue("missing \"$wanted\"; rows: $rows", wanted in rows)

        // "Start the app", then each section's title followed by its frames' names, then "Style kit".
        assertEquals(
            "Frames' sections differ from the Figma's",
            FigmaManifest.sections.map { it.slug to it.title },
            Frames.sections.map { it.slug to it.title },
        )
        val expected = listOf(START) + Frames.sections.flatMap { s -> listOf(s.title) + s.frames.map { it.name } } + KIT
        assertEquals(expected, rows)
    }

    @Test
    fun unknownOpenIdShowsList() {
        val list = launchList(open = "nope")
        assertFalse("the list finished", list.isFinishing)
        assertNull("the list started another screen", shadowOf(list).nextStartedActivity)
        val rows = rowTexts(list)
        assertTrue("missing \"$START\"; rows: $rows", rows.firstOrNull() == START)
    }

    @Test
    fun opensRegisteredFrameById() {
        assumeTrue("no frames are registered yet", Frames.all.isNotEmpty())
        for (frame in Frames.all) {
            val list = launchList(open = frame.id)
            val started = shadowOf(list).nextStartedActivity
            assertNotNull("${frame.id}: the list opened nothing", started)
            val wanted = frame.open(list)
            assertEquals("${frame.id}: opened the wrong screen", wanted.component, started.component)
            assertEquals("${frame.id}: opened with the wrong extras", extrasOf(wanted), extrasOf(started))
            assertTrue("${frame.id}: not opened with FLAG_ACTIVITY_CLEAR_TOP", started.flags and Intent.FLAG_ACTIVITY_CLEAR_TOP != 0)
        }
    }

    private fun launchList(open: String? = null): Activity {
        val intent = Intent(ApplicationProvider.getApplicationContext(), ScreenListActivity::class.java)
        if (open != null) intent.putExtra(Extras.OPEN, open)
        val list = Robolectric.buildActivity(ScreenListActivity::class.java, intent).setup().get()
        FrameHarness.idle()
        return list
    }

    /** The text of every row of the list, top to bottom, scrolling each one into view. Empty when there's no list. */
    private fun rowTexts(activity: Activity): List<String> {
        val list = findList(activity.window.decorView) ?: return emptyList()
        val count = list.adapter?.itemCount ?: 0
        return List(count) { position ->
            list.scrollToPosition(position)
            FrameHarness.idle()
            val row = list.findViewHolderForAdapterPosition(position) ?: error("row $position isn't laid out")
            FrameHarness.textOf(row.itemView)
        }
    }

    private fun findList(view: View): RecyclerView? = when (view) {
        is RecyclerView -> view
        is ViewGroup -> (0 until view.childCount).firstNotNullOfOrNull { findList(view.getChildAt(it)) }
        else -> null
    }

    @Suppress("DEPRECATION")
    private fun extrasOf(intent: Intent): Map<String, Any?> {
        val extras: Bundle = intent.extras ?: return emptyMap()
        return extras.keySet().associateWith { extras.get(it) }
    }

    private companion object {
        const val START = "Start the app"
        const val KIT = "Style kit"
    }
}
