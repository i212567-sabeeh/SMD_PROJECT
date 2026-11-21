# LeagueX

The Android frontend of LeagueX, an app for running and following local football and futsal leagues and
tournaments. It is built with XML layouts: all 80 screens of the Figma design, with the app's sample data. Nothing is
sent to a server or saved yet, because this is the frontend only.

## Open and run it

1. Open the `LeagueX` folder in Android Studio. The project uses Android Gradle Plugin 9.4.1, Gradle 9.6, compile SDK
   37 and min SDK 24. Android Studio's own JDK is enough.
2. Wait for the Gradle sync to finish.
3. Pick a phone or a virtual device, and press **Run 'app'**.

The app opens on **LeagueX screens**.

## Reach every screen

The **LeagueX screens** list groups the 80 Figma frames under the Figma's nine sections. Tap a row to open that frame:
the screen in the state the Figma draws, with its sheet or menu already open where the frame shows one.

- **Start the app** runs LeagueX from the splash screen, with real navigation between screens. Taps go where the
  design's main flows go: following a league, setting up a league or tournament, recording a match, working offline,
  registering a team and joining one.
- **Style kit** shows the shared parts: colours, type, buttons, fields, chips, rows and cards.

## How it's built

- **Language:** Kotlin, with XML layouts and view binding, and Material 3 components.
- **Screens:** one activity per screen. A screen's other states, such as Home's six versions, are the same activity
  opened with a different version.
- **Layout files** (`app/src/main/res/layout`):
  - `activity_*`: one per screen.
  - `include_*`: shared parts.
  - `item_*`: list rows.
  - `sheet_*`: bottom sheets.

  Each starts with a comment naming its Figma frame.
- **Code packages** (`app/src/main/java/com/leaguex/app`):
  - One package per area of the design: `start`, `home`, `league`, `news`, `players`, `captains`, `organizer`,
    `matchday` and `across`.
  - `kit`: binders for the shared parts.
  - `data`: the sample data.
  - `screens`: the screen list, with every frame registered in `screens/frames`.

## Tests

`./gradlew :app:testDebugUnitTest` runs the tests on the JVM with Robolectric, with no emulator needed.

| Test | What it checks |
|---|---|
| One `…FramesTest` per Figma section | Each frame is registered and shows every text the Figma frame shows. Its status bar and bottom bar match the Figma, and every tap on it is safe. |
| `FlowsTest` | Walks the design's main flows by the labels people tap. |
| `BackTest` | What the system back button does. |
| `DarkModeTest` | The app keeps its light look when the phone is in dark mode. |
| `RegistryTest` | The screen list is exactly the Figma's 80 frames, in order, and each one opens. |
| `SmallPhoneTest` | Every frame on a 360 × 640 dp phone with text at 130%: no text is cut off and nothing runs off a screen that doesn't scroll. |

To compare with the Figma, run
`./gradlew :app:testDebugUnitTest --tests com.leaguex.app.FrameShotsTest -Pshots=<section|all|kit>`. It draws each
frame into `shots/`. Then `node tools/png.mjs vs <section>` puts each one beside its Figma export.

## Every Figma frame

| Figma frame | Activity | Version | Layout |
|---|---|---|---|
| 01 Splash | `SplashActivity` | STILL | `activity_splash.xml` |
| 02 Welcome | `WelcomeActivity` | default | `activity_welcome.xml` |
| 03 Sign up | `SignUpActivity` | default | `activity_sign_up.xml` |
| 04 Log in | `LogInActivity` | default | `activity_log_in.xml` |
| 05 Forgot password | `ForgotPasswordActivity` | FORM | `activity_forgot_password.xml` |
| 05 Forgot password / Check your email | `ForgotPasswordActivity` | CHECK_EMAIL | `activity_forgot_password.xml` |
| 06 Get started | `GetStartedActivity` | default | `activity_get_started.xml` |
| 07 Set up your profile | `SetUpProfileActivity` | default | `activity_set_up_profile.xml` |
| 08 Home / League | `HomeActivity` | LEAGUE | `activity_home.xml` |
| 08 Home / Tournament | `HomeActivity` | TOURNAMENT | `activity_home.xml` |
| 08 Home / Live match | `HomeActivity` | LIVE | `activity_home.xml` |
| 08 Home / Nothing followed | `HomeActivity` | NOTHING | `activity_home.xml` |
| 08 Home / Offline | `HomeActivity` | OFFLINE | `activity_home.xml` |
| 08 Home / Organizer | `HomeActivity` | ORGANIZER | `activity_home.xml` |
| 09 Switch league | `HomeActivity` | LEAGUE | `activity_home.xml`; sheet `sheet_switch_league.xml` |
| 12 League page | `LeagueActivity` | default | `activity_league.xml` |
| 13 Matches | `MatchesActivity` | default | `activity_matches.xml` |
| 14 Match page / Before kick-off | `MatchActivity` | BEFORE | `activity_match.xml` |
| 14 Match page / Live | `MatchActivity` | LIVE | `activity_match.xml` |
| 14 Match page / Full time | `MatchActivity` | FULL_TIME | `activity_match.xml` |
| 14 Match page / Won on penalties | `MatchActivity` | PENALTIES | `activity_match.xml` |
| 14 Match page / Organizer | `MatchActivity` | ORGANIZER | `activity_match.xml` |
| 15 Table | `StandingsActivity` | TABLE | `activity_standings.xml` |
| 16 Bracket | `StandingsActivity` | BRACKET | `activity_standings.xml` |
| 17 Stats | `StandingsActivity` | STATS | `activity_standings.xml` |
| 10 Search | `SearchActivity` | default | `activity_search.xml` |
| 11 Filters | `SearchActivity` | default | `activity_search.xml`; sheet `sheet_filters.xml` |
| 18 Teams | `TeamsActivity` | default | `activity_teams.xml` |
| 19 Team page | `TeamActivity` | DEFAULT | `activity_team.xml` |
| 19 Team page / Captain | `TeamActivity` | CAPTAIN | `activity_team.xml` |
| 20 Player page | `PlayerActivity` | default | `activity_player.xml` |
| 21 Announcements | `AnnouncementsActivity` | default | `activity_announcements.xml` |
| 22 Announcement detail | `AnnouncementActivity` | default | `activity_announcement.xml` |
| 23 Notifications | `NotificationsActivity` | default | `activity_notifications.xml` |
| 24 My profile | `ProfileActivity` | PLAYER | `activity_profile.xml` |
| 24 My profile / Organizer | `ProfileActivity` | ORGANIZER | `activity_profile.xml` |
| 25 Edit profile | `EditProfileActivity` | DEFAULT | `activity_edit_profile.xml` |
| 25 Edit profile / Saved offline | `EditProfileActivity` | SAVED_OFFLINE | `activity_edit_profile.xml` |
| 26 Join a team | `JoinTeamActivity` | default | `activity_join_team.xml` |
| 27 Team invite | `TeamInviteActivity` | default | `activity_team_invite.xml` |
| 28 Register a team | `RegisterTeamActivity` | DEFAULT | `activity_register_team.xml` |
| 28 Register a team / Waiting for approval | `RegisterTeamActivity` | WAITING | `activity_register_team.xml` |
| 29 Manage squad | `ManageSquadActivity` | default | `activity_manage_squad.xml`; menu `menu_squad_player.xml` |
| 30 Add players | `AddPlayersActivity` | default | `activity_add_players.xml` |
| 31 Edit team | `EditTeamActivity` | default | `activity_edit_team.xml` |
| 32 Create / Type | `CreateTypeActivity` | default | `activity_create_type.xml` |
| 33 Create / Details | `CreateDetailsActivity` | LEAGUE | `activity_create_details.xml` |
| 34 Create / Rules, league | `CreateRulesActivity` | LEAGUE | `activity_create_rules.xml` |
| 34 Create / Rules, tournament | `CreateRulesActivity` | TOURNAMENT | `activity_create_rules.xml` |
| 35 Create / Review | `CreateReviewActivity` | LEAGUE | `activity_create_review.xml` |
| 36 Published | `PublishedActivity` | default | `activity_published.xml` |
| 37 Organizer dashboard | `DashboardActivity` | default | `activity_dashboard.xml` |
| 38 Team requests | `TeamRequestsActivity` | default | `activity_team_requests.xml` |
| 39 Generate fixtures | `FixturesActivity` | default | `activity_fixtures.xml` |
| 40 Draw | `DrawActivity` | default | `activity_draw.xml` |
| 41 Edit match | `DashboardActivity` | default | `activity_dashboard.xml`; sheet `sheet_edit_match.xml` |
| 42 Start playoffs | `StartPlayoffsActivity` | default | `activity_start_playoffs.xml` |
| 43 Post announcement | `PostAnnouncementActivity` | default | `activity_post_announcement.xml` |
| 44 League settings | `LeagueSettingsActivity` | default | `activity_league_settings.xml` |
| 45 Line-ups | `LineupsActivity` | default | `activity_lineups.xml` |
| 46 Record match / First half | `RecordMatchActivity` | FIRST_HALF | `activity_record_match.xml` |
| 46 Record match / Half time | `RecordMatchActivity` | HALF_TIME | `activity_record_match.xml` |
| 46 Record match / Extra time | `RecordMatchActivity` | EXTRA_TIME | `activity_record_match.xml` |
| 46 Record match / Offline | `RecordMatchActivity` | OFFLINE | `activity_record_match.xml` |
| 47 Add goal | `RecordMatchActivity` | OFFLINE | `activity_record_match.xml`; sheet `sheet_add_goal.xml` |
| 48 Add card | `RecordMatchActivity` | OFFLINE | `activity_record_match.xml`; sheet `sheet_add_card.xml` |
| 49 Add substitution | `RecordMatchActivity` | OFFLINE | `activity_record_match.xml`; sheet `sheet_add_sub.xml` |
| 50 Penalty shootout | `PenaltiesActivity` | default | `activity_penalties.xml` |
| 51 Confirm full time | `ConfirmFullTimeActivity` | default | `activity_confirm_full_time.xml` |
| 52 Sync status | `SyncStatusActivity` | WAITING | `activity_sync_status.xml` |
| 53 Photo upload / Pick | `EditProfileActivity` | DEFAULT | `activity_edit_profile.xml`; sheet `sheet_photo.xml` |
| 53 Photo upload / Uploading | `EditProfileActivity` | UPLOADING | `activity_edit_profile.xml` |
| 54 Push notifications | `LockScreenActivity` | default | `activity_lock_screen.xml` |
| 55 Settings | `SettingsActivity` | default | `activity_settings.xml` |
| 56 Notification settings | `NotificationSettingsActivity` | default | `activity_notification_settings.xml` |
| 57 Empty / No search results | `SearchActivity` | NO_RESULTS | `activity_search.xml` |
| 57 Empty / No matches yet | `MatchesActivity` | NO_MATCHES | `activity_matches.xml` |
| 57 Loading / Images | `TeamActivity` | LOADING | `activity_team.xml` |
| 57 Error / No connection | `LeagueActivity` | NO_CONNECTION | `activity_league.xml` |
| 57 Error / Upload failed | `SyncStatusActivity` | UPLOAD_FAILED | `activity_sync_status.xml` |

## Licences

- **Fonts:** Anton and Archivo, under the SIL Open Font License 1.1. The licence texts are in `licenses/`.
- **Icons:** Material Symbols by Google, under the Apache License 2.0.
