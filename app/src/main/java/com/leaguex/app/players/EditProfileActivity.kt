package com.leaguex.app.players

import android.os.Bundle
import androidx.core.view.isVisible
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.data.SampleData
import com.leaguex.app.databinding.ActivityEditProfileBinding
import com.leaguex.app.databinding.SheetPhotoBinding
import com.leaguex.app.kit.avatar
import com.leaguex.app.kit.bind
import com.leaguex.app.kit.showSavedOffline
import com.leaguex.app.kit.showSheet

/**
 * 25 Edit profile's versions: the form, 25 Saved offline after saving with no connection, and 53 Photo upload /
 * Uploading while a new photo goes up (53 Pick is the photo sheet over the form).
 */
enum class EditProfileVersion { DEFAULT, SAVED_OFFLINE, UPLOADING }

/** 25 Edit profile: photo, positions, phone and where you live. Nothing is sent anywhere yet. */
class EditProfileActivity : LeagueXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(b.root)
        fitSystemBars(top = b.root, bottom = b.footer)
        b.top.title.setText(R.string.edit_title)
        backOn(b.top.back)

        val me = SampleData.ME
        b.avatar.avatar(me.name)
        b.phone.setText(me.phone)
        b.province.setText(me.province, false)
        b.city.setText(me.city, false)
        b.changePhoto.setOnClickListener { photoSheet() }

        when (versionOr(EditProfileVersion.DEFAULT)) {
            EditProfileVersion.SAVED_OFFLINE -> {
                b.offline.root.isVisible = true
                b.content.setPadding(b.content.paddingLeft, dp(16), b.content.paddingRight, b.content.paddingBottom)
                b.changePhoto.isVisible = false
                b.photoWaiting.isVisible = true
                b.root.showSavedOffline(anchor = b.footer)
            }
            EditProfileVersion.UPLOADING -> {
                b.photo.isVisible = false
                b.uploading.isVisible = true
                b.progressText.text = getString(R.string.photo_uploading, UPLOADED_PERCENT)
                b.progress.progress = UPLOADED_PERCENT
                b.provinceField.isVisible = false
                b.cityField.isVisible = false
                b.cancelUpload.setOnClickListener {
                    go(EditProfileActivity::class, EditProfileVersion.DEFAULT)
                    finish()
                }
            }
            EditProfileVersion.DEFAULT -> if (sheetToOpen == SHEET_PHOTO) photoSheet()
        }
        b.save.setOnClickListener {
            go(EditProfileActivity::class, EditProfileVersion.SAVED_OFFLINE)
            finish()
        }
    }

    /** 53 Photo upload / Pick: taking or choosing a photo starts its upload. */
    private fun photoSheet() {
        val sheet = SheetPhotoBinding.inflate(layoutInflater)
        sheet.head.sheetTitle.setText(R.string.photo_title)
        sheet.take.bind(R.drawable.ic_photo_camera, getString(R.string.photo_take))
        sheet.gallery.bind(R.drawable.ic_photo_library, getString(R.string.photo_gallery))
        sheet.remove.bind(R.drawable.ic_delete, getString(R.string.photo_remove), danger = true)
        val dialog = showSheet(sheet.root)
        val upload = { _: android.view.View ->
            dialog.dismiss()
            go(EditProfileActivity::class, EditProfileVersion.UPLOADING)
        }
        sheet.take.root.setOnClickListener(upload)
        sheet.gallery.root.setOnClickListener(upload)
        sheet.remove.root.setOnClickListener { dialog.dismiss() }
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    companion object {
        /** Extras.SHEET value that opens 53 Photo upload / Pick over the form. */
        const val SHEET_PHOTO = "photo"

        private const val UPLOADED_PERCENT = 64
    }
}
