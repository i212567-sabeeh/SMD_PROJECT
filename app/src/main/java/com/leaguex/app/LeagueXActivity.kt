package com.leaguex.app

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.leaguex.app.data.Session
import com.leaguex.app.home.HomeActivity
import com.leaguex.app.league.MatchesActivity
import com.leaguex.app.league.StandingsActivity
import com.leaguex.app.league.StandingsVersion
import com.leaguex.app.news.TeamsActivity
import com.leaguex.app.players.ProfileActivity
import java.util.UUID
import kotlin.reflect.KClass

/** The bottom-bar tabs. */
enum class Tab { HOME, MATCHES, STANDINGS, TEAMS, PROFILE }

/** The tab's item in menu/bottom_nav. */
private val Tab.menuItem: Int
    get() = when (this) {
        Tab.HOME -> R.id.nav_home
        Tab.MATCHES -> R.id.nav_matches
        Tab.STANDINGS -> R.id.nav_standings
        Tab.TEAMS -> R.id.nav_teams
        Tab.PROFILE -> R.id.nav_profile
    }

/** The tab's screen. */
private val Tab.screen: KClass<out Activity>
    get() = when (this) {
        Tab.HOME -> HomeActivity::class
        Tab.MATCHES -> MatchesActivity::class
        Tab.STANDINGS -> StandingsActivity::class
        Tab.TEAMS -> TeamsActivity::class
        Tab.PROFILE -> ProfileActivity::class
    }

/** The version a tab opens in: Standings shows the competition picked on Home, the league's table or the cup's bracket. */
private val Tab.version: Enum<*>?
    get() = when (this) {
        Tab.STANDINGS -> if (Session.cupPicked) StandingsVersion.BRACKET else StandingsVersion.TABLE
        else -> null
    }

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
        passFlowOn(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        passFlowOn(intent)
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

    /**
     * Wires the bottom bar (include_bottom_nav): checks this screen's [tab], and selecting another tab opens that
     * tab's screen without an animation. This screen keeps its own tab checked for when it comes back.
     */
    protected fun bottomNav(nav: BottomNavigationView) {
        val tab = tab
        if (tab != null) nav.selectedItemId = tab.menuItem
        nav.setOnItemSelectedListener { item ->
            val target = Tab.entries.firstOrNull { it.menuItem == item.itemId }
            if (target != null && target != tab) openTab(target)
            false
        }
        nav.setOnItemReselectedListener { }
    }

    /**
     * Opens [target]'s screen without an animation. Home comes back from under the tab, as it was; another tab opens in
     * the competition picked on Home. A tab other than Home closes as it hands over, so at most one tab sits above
     * Home, and back from a tab shows Home again.
     */
    private fun openTab(target: Tab) {
        val intent = screen(target.screen, target.version)
        if (target == Tab.HOME) intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent, ActivityOptionsCompat.makeCustomAnimation(this, 0, 0).toBundle())
        if (tab != Tab.HOME) finish()
    }

    /**
     * Ends a flow that started at [first]: brings [first] back, which closes every step above it, and [first] then
     * opens [then] and closes too. Back from [then] goes to wherever the flow was started from.
     *
     * [then] stays in memory. The Intent to [first] carries only a one-time token for it, so an Intent from outside
     * the app (the screen list is exported) can't make a screen open something else.
     */
    protected fun endFlow(first: KClass<out Activity>, then: Intent) {
        val token = UUID.randomUUID().toString()
        pendingHandOver = token to then
        startActivity(
            screen(first).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP).putExtra(Extras.THEN, token),
        )
        finish()
    }

    /** Goes back to [target] if it's open below this screen (closing what's above it), or opens it; this screen closes. */
    protected fun returnTo(target: KClass<out Activity>, version: Enum<*>? = null) {
        startActivity(screen(target, version).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
        finish()
    }

    /**
     * A flow's first screen, brought back by [endFlow], opens what comes after the flow and closes. Only the token of
     * the hand-over [endFlow] made counts, once; anything else in [Extras.THEN] is ignored.
     */
    private fun passFlowOn(intent: Intent) {
        val token = intent.getStringExtra(Extras.THEN) ?: return
        intent.removeExtra(Extras.THEN)
        val (expected, then) = pendingHandOver ?: return
        if (token != expected) return
        pendingHandOver = null
        startActivity(then)
        finish()
    }

    private companion object {
        /** The hand-over [endFlow] made and its first screen hasn't passed on yet: its one-time token and what to open. */
        var pendingHandOver: Pair<String, Intent>? = null
    }

    /** Opens [target], optionally in a version or with a sheet showing. */
    protected fun go(target: KClass<out Activity>, version: Enum<*>? = null, sheet: String? = null) {
        startActivity(screen(target, version, sheet))
    }
}
