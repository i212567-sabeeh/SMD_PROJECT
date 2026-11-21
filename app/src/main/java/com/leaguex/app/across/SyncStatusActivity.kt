package com.leaguex.app.across

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivitySyncStatusBinding
import com.leaguex.app.kit.StatusKind
import com.leaguex.app.kit.keepTogether
import com.leaguex.app.kit.rows.SyncItem
import com.leaguex.app.kit.rows.SyncItemAdapter
import com.leaguex.app.kit.rows.showRows
import com.leaguex.app.screen
import kotlin.math.roundToInt

/** 52 Sync status while offline (WAITING) and 57 Error / Upload failed once back online (UPLOAD_FAILED). */
enum class SyncStatusVersion { WAITING, UPLOAD_FAILED }

/**
 * 52 Sync status: what's saved on this phone and its upload. Offline, three changes wait and Sync now tries them;
 * back online they're uploaded, the team logo failed, and Retry upload tries again.
 */
class SyncStatusActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivitySyncStatusBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.footer)
        b.top.title.setText(R.string.sync_title)
        backOn(b.top.back)

        val waiting = versionOr(SyncStatusVersion.WAITING) == SyncStatusVersion.WAITING
        b.offline.root.isVisible = waiting
        b.content.updatePadding(top = dp(if (waiting) WAITING_TOP else FAILED_TOP))
        b.summaryGlyph.isVisible = waiting
        b.summaryIcon.isVisible = !waiting
        if (waiting) {
            b.summaryTitle.text = keepTogether(getString(R.string.sync_last, "7:41 pm"))
            b.summaryBody.text = resources.getQuantityString(R.plurals.sync_waiting_count, CHANGES, CHANGES)
        } else {
            b.summary.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.error_bg))
            b.summaryTitle.text = resources.getQuantityString(R.plurals.sync_back_online, 1, 1)
            b.summaryBody.text = keepTogether(resources.getQuantityString(R.plurals.sync_uploaded_at, CHANGES, CHANGES, "8:05 pm"))
        }
        b.itemsLabel.setText(if (waiting) R.string.sync_waiting else R.string.sync_uploaded)
        b.items.showRows(SyncItemAdapter(changes(waiting)))

        // Sync now finds the connection back and one upload failing; Retry upload (or retrying the logo) starts over.
        val next = if (waiting) SyncStatusVersion.UPLOAD_FAILED else SyncStatusVersion.WAITING
        val sync = {
            go(SyncStatusActivity::class, next)
            finish()
        }
        val logo = SyncItem(
            R.drawable.ic_image, getString(R.string.sync_item_logo, SampleData.teamName("MAR")),
            getString(R.string.sync_saved, "7:38 pm"), failed = true,
        )
        b.failedItems.showRows(SyncItemAdapter(listOf(logo), onRetry = { sync() }))
        b.sync.setText(if (waiting) R.string.sync_now else R.string.sync_retry)
        b.sync.setIconResource(if (waiting) R.drawable.ic_sync else R.drawable.ic_refresh)
        b.sync.setOnClickListener { sync() }
    }

    /** The goal, the substitution and the profile photo: waiting, with when they were saved, or synced. */
    private fun changes(waiting: Boolean): List<SyncItem> {
        val match = "MAR vs RAW"
        val status = if (waiting) StatusKind.WAITING else StatusKind.SYNCED
        val statusText = getString(if (waiting) R.string.sync_waiting else R.string.sync_synced)
        fun meta(where: String, saved: String) = if (waiting) getString(R.string.sync_saved_at, where, saved) else where
        return listOf(
            SyncItem(
                R.drawable.ic_sports_soccer, getString(R.string.sync_item_goal, SampleData.ME.name, "29'"),
                meta(match, "7:46 pm"), status, statusText,
            ),
            SyncItem(R.drawable.ic_swap_vert, getString(R.string.sync_item_sub, "30'"), meta(match, "7:47 pm"), status, statusText),
            SyncItem(R.drawable.ic_account_circle, getString(R.string.sync_item_photo), meta(SampleData.ME.name, "7:52 pm"), status, statusText),
        )
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).roundToInt()

    private companion object {
        const val CHANGES = 3
        const val WAITING_TOP = 16
        const val FAILED_TOP = 8
    }
}

/** The offline banner (ui.js offlineBanner) is a way into 52 Sync status: tapping it shows what's waiting to upload. */
fun View.opensSyncStatus() {
    setOnClickListener { context.startActivity(context.screen(SyncStatusActivity::class, SyncStatusVersion.WAITING)) }
}
