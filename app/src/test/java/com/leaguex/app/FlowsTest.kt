package com.leaguex.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.leaguex.app.FlowHarness.next
import com.leaguex.app.FlowHarness.open
import com.leaguex.app.FlowHarness.sheet
import com.leaguex.app.FlowHarness.tap
import com.leaguex.app.across.SyncStatusActivity
import com.leaguex.app.across.SyncStatusVersion
import com.leaguex.app.captains.AddPlayersActivity
import com.leaguex.app.captains.ManageSquadActivity
import com.leaguex.app.captains.RegisterTeamActivity
import com.leaguex.app.captains.RegisterTeamVersion
import com.leaguex.app.home.HomeActivity
import com.leaguex.app.home.HomeVersion
import com.leaguex.app.league.LeagueActivity
import com.leaguex.app.league.MatchActivity
import com.leaguex.app.league.MatchVersion
import com.leaguex.app.matchday.ConfirmFullTimeActivity
import com.leaguex.app.matchday.LineupsActivity
import com.leaguex.app.matchday.RecordMatchActivity
import com.leaguex.app.matchday.RecordMatchVersion
import com.leaguex.app.news.SearchActivity
import com.leaguex.app.news.TeamActivity
import com.leaguex.app.news.TeamVersion
import com.leaguex.app.organizer.CreateDetailsActivity
import com.leaguex.app.organizer.CreateKind
import com.leaguex.app.organizer.CreateReviewActivity
import com.leaguex.app.organizer.CreateRulesActivity
import com.leaguex.app.organizer.CreateTypeActivity
import com.leaguex.app.organizer.DashboardActivity
import com.leaguex.app.organizer.DrawActivity
import com.leaguex.app.organizer.FixturesActivity
import com.leaguex.app.organizer.PublishedActivity
import com.leaguex.app.organizer.TeamRequestsActivity
import com.leaguex.app.players.EditProfileActivity
import com.leaguex.app.players.JoinTeamActivity
import com.leaguex.app.players.ProfileActivity
import com.leaguex.app.players.ProfileVersion
import com.leaguex.app.players.TeamInviteActivity
import com.leaguex.app.start.GetStartedActivity
import com.leaguex.app.start.SetUpProfileActivity
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * The main flows of the screen design ("Main flows"), tapped through by the labels people see. Each step names the
 * screen and version the tap must open.
 */
@RunWith(AndroidJUnit4::class)
@Config(qualifiers = "w412dp-h917dp-xxhdpi")
class FlowsTest {

    @Test
    fun spectator() {
        // Get started → (your profile, skipped) → Home → Search with filters → League page → Follow → Home → Match page.
        open(GetStartedActivity::class).tap("Follow leagues and tournaments")
        next(SetUpProfileActivity::class).tap("Skip")
        next(HomeActivity::class, HomeVersion.LEAGUE).tap("Search")
        val search = next(SearchActivity::class)
        search.tap("More filters")
        assertNotNull("the filters sheet opens", sheet())
        search.tap("Show results")
        assertNull("Show results closes the filters", sheet())
        search.tap("Leagues")
        search.tap("Islamabad Futsal League")
        next(LeagueActivity::class).tap("Follow")
        next(HomeActivity::class, HomeVersion.LEAGUE).tap("Match details")
        next(MatchActivity::class, MatchVersion.BEFORE)
    }

    @Test
    fun organizerSettingUpALeague() {
        // Create, steps 1 to 4 → Published → (dashboard) → Team requests, then Generate fixtures.
        open(CreateTypeActivity::class).tap("Continue")
        next(CreateDetailsActivity::class, CreateKind.LEAGUE).tap("Continue")
        next(CreateRulesActivity::class, CreateKind.LEAGUE).tap("Continue")
        next(CreateReviewActivity::class, CreateKind.LEAGUE).tap("Publish league")
        next(PublishedActivity::class, CreateKind.LEAGUE).tap("Go to dashboard")
        val dashboard = next(DashboardActivity::class)
        dashboard.tap("Review")
        next(TeamRequestsActivity::class)
        dashboard.tap("Generate fixtures")
        next(FixturesActivity::class)
    }

    @Test
    fun organizerSettingUpATournament() {
        // The same steps for a knockout tournament end at the Draw.
        val type = open(CreateTypeActivity::class)
        type.tap("Knockout tournament")
        type.tap("Continue")
        next(CreateDetailsActivity::class, CreateKind.TOURNAMENT).tap("Continue")
        next(CreateRulesActivity::class, CreateKind.TOURNAMENT).tap("Continue")
        next(CreateReviewActivity::class, CreateKind.TOURNAMENT).tap("Publish tournament")
        next(PublishedActivity::class, CreateKind.TOURNAMENT).tap("Make the draw")
        next(DrawActivity::class)
    }

    @Test
    fun organizerMatchDay() {
        // Home (organizer) → dashboard → Line-ups → Record match (a goal) → half time → 2nd half → Confirm full time → match page.
        open(HomeActivity::class, HomeVersion.ORGANIZER).tap("Organizer dashboard")
        next(DashboardActivity::class).tap("Set line-ups")
        next(LineupsActivity::class).tap("Start match")
        val firstHalf = next(RecordMatchActivity::class, RecordMatchVersion.FIRST_HALF)
        firstHalf.tap("Goal")
        assertNotNull("Goal opens Add goal", sheet())
        firstHalf.tap("Save goal")
        assertNull("Save goal closes the sheet", sheet())
        firstHalf.tap("End half")
        next(RecordMatchActivity::class, RecordMatchVersion.HALF_TIME).tap("Start 2nd half")
        next(RecordMatchActivity::class, RecordMatchVersion.OFFLINE).tap("End match")
        next(ConfirmFullTimeActivity::class).tap("Finish match")
        next(MatchActivity::class, MatchVersion.FULL_TIME)
    }

    @Test
    fun organizerOffline() {
        // Record match offline → the offline banner → Sync status → Sync now → back online, one upload failed.
        open(RecordMatchActivity::class, RecordMatchVersion.OFFLINE).tap("You're offline. Showing what's saved on this phone.")
        next(SyncStatusActivity::class, SyncStatusVersion.WAITING).tap("Sync now")
        next(SyncStatusActivity::class, SyncStatusVersion.UPLOAD_FAILED)
    }

    @Test
    fun captain() {
        // Register a team → waiting for approval → Team page (captain) → Manage squad → Add players.
        open(RegisterTeamActivity::class).tap("Send request")
        next(RegisterTeamActivity::class, RegisterTeamVersion.WAITING).tap("Back to my team")
        next(TeamActivity::class, TeamVersion.CAPTAIN).tap("Manage squad")
        next(ManageSquadActivity::class).tap("Add players")
        next(AddPlayersActivity::class)
    }

    @Test
    fun player() {
        // Join a team → Team invite → My profile → Edit profile.
        open(JoinTeamActivity::class).tap("Join team")
        next(TeamInviteActivity::class).tap("Accept")
        next(ProfileActivity::class, ProfileVersion.PLAYER).tap("Edit profile")
        next(EditProfileActivity::class)
    }
}
