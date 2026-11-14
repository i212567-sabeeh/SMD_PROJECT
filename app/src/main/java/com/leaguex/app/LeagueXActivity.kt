package com.leaguex.app

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import kotlin.reflect.KClass

/** The bottom-bar tabs. */
enum class Tab { HOME, MATCHES, STANDINGS, TEAMS, PROFILE }

/**
 * Every screen activity extends this. It draws edge to edge with transparent system bars, and gives
 * screens their version, sheet and menu extras plus the helpers they share.
 */
abstract class LeagueXActivity : AppCompatActivity() {

    /** The bottom-bar tab this screen belongs to, or null when it has no bottom bar. */
    protected open val tab: Tab? get() = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // The design is light only, so the bars start with dark icons whatever the system theme is.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
    }

    /** The version named by [Extras.VERSION], or [default] when there's none or the name is unknown. */
    protected inline fun <reified E : Enum<E>> versionOr(default: E): E {
        val name = intent.getStringExtra(Extras.VERSION) ?: return default
        return enumValues<E>().firstOrNull { it.name == name } ?: default
    }

    /** The sheet named by [Extras.SHEET], to open over this screen. */
    protected val sheetToOpen: String? get() = intent.getStringExtra(Extras.SHEET)

    /** True when [Extras.MENU] asks for the screen's menu to be showing. */
    protected val menuToOpen: Boolean get() = intent.getBooleanExtra(Extras.MENU, false)

    /** Light status-bar icons on a blue top, dark ones on a light top. */
    protected fun statusIcons(onBlue: Boolean) {
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = !onBlue
    }

    /**
     * Pads [top] by the status bar's height and [bottom] by the navigation bar's, on top of the padding
     * they already have. Call once, after setContentView. A BottomNavigationView pads itself, so it isn't
     * passed as [bottom].
     */
    protected fun fitSystemBars(top: View?, bottom: View?) {
        val topPadding = top?.paddingTop ?: 0
        val bottomPadding = bottom?.paddingBottom ?: 0
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { _, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            top?.updatePadding(top = topPadding + bars.top)
            bottom?.updatePadding(bottom = bottomPadding + bars.bottom)
            insets
        }
    }

    /** Tapping [view] does what the system back does. */
    protected fun backOn(view: View) {
        view.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    /** Opens [target], optionally in a version or with a sheet showing. */
    protected fun go(target: KClass<out Activity>, version: Enum<*>? = null, sheet: String? = null) {
        startActivity(screen(target, version, sheet))
    }
}
