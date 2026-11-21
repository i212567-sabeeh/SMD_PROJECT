package com.leaguex.app

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.leaguex.app.FlowHarness.open
import com.leaguex.app.FlowHarness.sheet
import com.leaguex.app.FlowHarness.tap
import com.leaguex.app.across.SettingsActivity
import com.leaguex.app.home.HomeActivity
import com.leaguex.app.home.HomeVersion
import com.leaguex.app.league.LeagueActivity
import com.leaguex.app.league.MatchesActivity
import com.leaguex.app.league.StandingsActivity
import com.leaguex.app.league.StandingsVersion
import com.leaguex.app.matchday.LineupsActivity
import com.leaguex.app.matchday.PenaltiesActivity
import com.leaguex.app.matchday.RecordMatchActivity
import com.leaguex.app.matchday.RecordMatchVersion
import com.leaguex.app.news.TeamsActivity
import com.leaguex.app.organizer.CreateKind
import com.leaguex.app.organizer.CreateReviewActivity
import com.leaguex.app.organizer.CreateTypeActivity
import com.leaguex.app.organizer.DashboardActivity
import com.leaguex.app.organizer.PublishedActivity
import com.leaguex.app.players.EditProfileActivity
import com.leaguex.app.players.EditProfileVersion
import com.leaguex.app.players.JoinTeamActivity
import com.leaguex.app.players.ProfileActivity
import com.leaguex.app.players.TeamInviteActivity
import com.leaguex.app.screens.ScreenListActivity
import com.leaguex.app.start.LogInActivity
import com.leaguex.app.start.SignUpActivity
import com.leaguex.app.start.WelcomeActivity
import kotlin.reflect.KClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowPopupMenu

/**
 * The screens a person passes through don't pile up: the bottom bar keeps one tab above Home, a finished flow closes
 * its steps, and going back to an earlier step returns to it instead of opening another copy.
 */
@RunWith(AndroidJUnit4::class)
@Config(qualifiers = "w412dp-h917dp-xxhdpi")
class BackStackTest {

    private val app: Application get() = ApplicationProvider.getApplicationContext()

    // The bottom bar.

    @Test
    fun leavingHomeForATabKeepsHome() {
        val home = open(HomeActivity::class, HomeVersion.LEAGUE)
        home.tap("Standings")
        started(StandingsActivity::class)
        assertFalse(home.isFinishing)
    }

    @Test
    fun switchingTabsClosesTheTabYouLeave() {
        val standings = open(StandingsActivity::class)
        standings.tap("Teams")
        started(TeamsActivity::class)
        assertTrue(standings.isFinishing)
    }

    @Test
    fun theHomeTabReturnsToTheHomeUnderneath() {
        val matches = open(MatchesActivity::class)
        matches.tap("Home")
        val home = started(HomeActivity::class)
        assertTrue("Home comes back rather than opening again", home.has(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
        assertNull("Home keeps the competition it shows", home.getStringExtra(Extras.VERSION))
        assertTrue(matches.isFinishing)
    }

    // The competition picked on Home.

    @Test
    fun standingsShowsTheCompetitionPickedOnHome() {
        open(HomeActivity::class, HomeVersion.TOURNAMENT).tap("Standings")
        assertEquals(StandingsVersion.BRACKET.name, started(StandingsActivity::class).getStringExtra(Extras.VERSION))
        open(HomeActivity::class, HomeVersion.LEAGUE).tap("Standings")
        assertEquals(StandingsVersion.TABLE.name, started(StandingsActivity::class).getStringExtra(Extras.VERSION))
    }

    @Test
    fun aTabsCompetitionNameSwitchesCompetition() {
        val standings = open(StandingsActivity::class, StandingsVersion.TABLE)
        standings.tap("Islamabad Futsal League")
        assertNotNull("Switch league opens", sheet())
        standings.tap("Capital Futsal Cup")
        assertEquals(StandingsVersion.BRACKET.name, started(StandingsActivity::class).getStringExtra(Extras.VERSION))
        assertTrue(standings.isFinishing)
    }

    // Finished flows.

    @Test
    fun publishingClosesTheCreateSteps() {
        val published = open(PublishedActivity::class, CreateKind.LEAGUE)
        published.tap("Go to dashboard")
        endsFlowAt(published, CreateTypeActivity::class, then = DashboardActivity::class)
    }

    @Test
    fun loggingInClosesTheSignInScreens() {
        val logIn = open(LogInActivity::class)
        logIn.tap("Log in")
        endsFlowAt(logIn, WelcomeActivity::class, then = HomeActivity::class)
    }

    @Test
    fun acceptingAnInviteClosesTheJoinSteps() {
        val invite = open(TeamInviteActivity::class)
        invite.tap("Accept")
        endsFlowAt(invite, JoinTeamActivity::class, then = ProfileActivity::class)
    }

    /**
     * Only a hand-over this app made counts: an Intent from outside, sent to the exported screen list with a forged
     * one, opens nothing and closes nothing.
     */
    @Test
    fun aForgedHandOverIsIgnored() {
        val forged = Intent(app, SettingsActivity::class.java)
        for (handOver in listOf<Any>(forged, "not-a-real-hand-over")) {
            val intent = app.screen(ScreenListActivity::class)
            when (handOver) {
                is Intent -> intent.putExtra(Extras.THEN, handOver)
                else -> intent.putExtra(Extras.THEN, handOver.toString())
            }
            val list = open(intent)
            assertNull("nothing opens for $handOver", shadowOf(app).nextStartedActivity)
            assertFalse("the screen list stays for $handOver", list.isFinishing)
        }
    }

    @Test
    fun startingTheMatchClosesLineUps() {
        val lineups = open(LineupsActivity::class)
        lineups.tap("Start match")
        started(RecordMatchActivity::class)
        assertTrue(lineups.isFinishing)
    }

    @Test
    fun aFinishedShootoutClosesTheRecording() {
        val recording = open(RecordMatchActivity::class, RecordMatchVersion.EXTRA_TIME)
        recording.tap("More options")
        val popup = ShadowPopupMenu.getLatestPopupMenu()
        val penalties = (0 until popup.menu.size()).map { popup.menu.getItem(it) }.first { it.title == "Penalty shootout" }
        shadowOf(popup).onMenuItemClickListener.onMenuItemClick(penalties)
        val launched = shadowOf(recording).nextStartedActivityForResult
        assertEquals(PenaltiesActivity::class.java.name, launched?.intent?.component?.className)
        assertTrue("the shootout reports back to the recording", (launched?.requestCode ?: -1) >= 0)
        val shootout = open(PenaltiesActivity::class)
        shootout.tap("Missed")
        shootout.tap("Finish match")
        assertEquals(Activity.RESULT_OK, shadowOf(shootout).resultCode)
        assertTrue(shootout.isFinishing)
    }

    // Going back to an earlier step.

    @Test
    fun aNewPhotoReplacesTheForm() {
        val form = open(EditProfileActivity::class, EditProfileVersion.DEFAULT)
        form.tap("Change photo")
        form.tap("Take photo")
        assertEquals(EditProfileVersion.UPLOADING.name, started(EditProfileActivity::class).getStringExtra(Extras.VERSION))
        assertTrue("the form doesn't stay underneath", form.isFinishing)
    }

    @Test
    fun reviewsEditReturnsToTheStep() {
        val review = open(CreateReviewActivity::class, CreateKind.TOURNAMENT)
        review.tap("Edit")
        val type = started(CreateTypeActivity::class)
        assertTrue("the step comes back rather than opening again", type.has(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
        assertEquals("with the type chosen", CreateKind.TOURNAMENT.name, type.getStringExtra(Extras.VERSION))
        assertTrue(review.isFinishing)
    }

    @Test
    fun signUpAndLogInSwapPlaces() {
        val signUp = open(SignUpActivity::class)
        signUp.tap("Log in")
        assertTrue(started(LogInActivity::class).has(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
        assertTrue(signUp.isFinishing)
    }

    // Following from Home.

    @Test
    fun followingASuggestionShowsItOnHome() {
        val home = open(HomeActivity::class, HomeVersion.NOTHING)
        home.tap("Follow")
        assertEquals(HomeVersion.LEAGUE.name, started(HomeActivity::class).getStringExtra(Extras.VERSION))
        home.tap("Capital Futsal Cup")
        started(LeagueActivity::class)
    }

    /** The Intent the last tap started, which must open [cls]. */
    private fun started(cls: KClass<out Activity>): Intent {
        val intent = shadowOf(app).nextStartedActivity
        assertNotNull("expected ${cls.simpleName} to open", intent)
        assertEquals(cls.java.name, intent.component?.className)
        return intent
    }

    /**
     * [from] ended its flow: it closed, and brought back [first] with everything above it closed, which then opens
     * [then] and closes too.
     */
    private fun endsFlowAt(from: Activity, first: KClass<out Activity>, then: KClass<out Activity>) {
        val back = started(first)
        assertTrue("${first.simpleName} comes back", back.has(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
        assertTrue("${from.javaClass.simpleName} closes", from.isFinishing)
        val passing = open(back)
        assertEquals("and then opens ${then.simpleName}", then.java.name, shadowOf(app).nextStartedActivity?.component?.className)
        assertTrue("${first.simpleName} closes once it has passed the flow on", passing.isFinishing)
    }

    private fun Intent.has(flags: Int) = this.flags and flags == flags
}
