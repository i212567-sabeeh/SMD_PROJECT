package com.leaguex.app.across

import android.content.Intent
import android.os.Bundle
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivitySettingsBinding
import com.leaguex.app.kit.bind
import com.leaguex.app.players.EditProfileActivity
import com.leaguex.app.screen
import com.leaguex.app.start.WelcomeActivity

/**
 * 55 Settings: the account, notifications, offline storage and help, then Log out, which goes back to Welcome with
 * nothing left behind it.
 */
class SettingsActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.content)
        b.top.title.setText(R.string.settings_title)
        backOn(b.top.back)

        b.account.bind(R.drawable.ic_person, R.string.settings_account, SampleData.ME.email) { go(EditProfileActivity::class) }
        b.notifications.bind(R.drawable.ic_notifications, R.string.settings_notifications, getString(R.string.settings_notifications_value)) {
            go(NotificationSettingsActivity::class)
        }
        b.offlineStorage.bind(R.drawable.ic_download_for_offline, R.string.settings_offline, getString(R.string.settings_offline_value, SAVED_MB)) {
            go(SyncStatusActivity::class, SyncStatusVersion.WAITING)
        }
        // Help has no page in the Figma, so its row only shows where help will be.
        b.help.bind(R.drawable.ic_help, R.string.settings_help, "")
        b.logOut.bind(R.drawable.ic_logout, R.string.settings_log_out, "", danger = true) {
            startActivity(screen(WelcomeActivity::class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        }
    }

    private companion object {
        const val SAVED_MB = 48
    }
}
