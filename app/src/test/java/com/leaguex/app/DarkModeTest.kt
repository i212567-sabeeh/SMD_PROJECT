package com.leaguex.app

import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.leaguex.app.FlowHarness.open
import com.leaguex.app.home.HomeActivity
import com.leaguex.app.home.HomeVersion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/** LeagueX has one light look: with the phone in dark mode, pages stay light and text stays ink. */
@RunWith(AndroidJUnit4::class)
@Config(qualifiers = "+night")
class DarkModeTest {

    @Test
    fun homeStaysLight() {
        val home = open(HomeActivity::class, HomeVersion.LEAGUE)
        assertEquals(
            "the phone is in dark mode",
            Configuration.UI_MODE_NIGHT_YES,
            home.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK,
        )
        val root = home.findViewById<View>(R.id.root)
        assertEquals("the page", PAGE, (root.background as ColorDrawable).color)
        val heading = textViews(root).firstOrNull { it.text.toString() == home.getString(R.string.home_table) }
        assertNotNull("Home shows its Table heading", heading)
        assertEquals("the Table heading", INK, heading!!.currentTextColor)
    }

    private fun textViews(view: View): List<TextView> = when (view) {
        is TextView -> listOf(view)
        is ViewGroup -> (0 until view.childCount).flatMap { textViews(view.getChildAt(it)) }
        else -> emptyList()
    }

    private companion object {
        /** colors.xml's page and ink, as the light look draws them. */
        const val PAGE = 0xFFF4F6FB.toInt()
        const val INK = 0xFF0F1533.toInt()
    }
}
