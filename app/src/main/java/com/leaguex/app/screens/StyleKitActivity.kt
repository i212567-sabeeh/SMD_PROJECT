package com.leaguex.app.screens

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.GridLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import com.leaguex.app.LeagueXActivity
import com.leaguex.app.R
import com.leaguex.app.databinding.ActivityStyleKitBinding
import com.leaguex.app.databinding.ItemKitSwatchBinding
import com.leaguex.app.databinding.ItemKitTypeBinding
import com.leaguex.app.databinding.SheetStyleKitBinding
import com.leaguex.app.kit.MatchClock
import com.leaguex.app.kit.ScoreState
import com.leaguex.app.kit.Scoreboard
import com.leaguex.app.kit.Tie
import com.leaguex.app.kit.avatar
import com.leaguex.app.kit.bind
import com.leaguex.app.kit.crest
import com.leaguex.app.kit.showSheet
import java.util.Locale
import kotlin.math.roundToInt

/**
 * "Style kit": the Figma's Style kit page as a screen, so the kit parts can be checked against it. The last row of
 * the screen list opens it.
 */
class StyleKitActivity : LeagueXActivity() {

    private lateinit var binding: ActivityStyleKitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStyleKitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fitSystemBars(top = binding.content, bottom = binding.content)
        addSwatches(binding.colours, R.array.kit_colour_names, COLOURS)
        addSwatches(binding.teamColours, R.array.kit_team_colour_names, TEAM_COLOURS)
        addTypeSamples()
        bindParts()
    }

    /** One swatch per colour: the colour in a 1dp line border, its name, and its hex value read from the colour. */
    private fun addSwatches(grid: GridLayout, names: Int, colours: List<Int>) {
        val dp = resources.displayMetrics.density
        resources.getStringArray(names).zip(colours).forEachIndexed { i, (name, colourRes) ->
            val colour = ContextCompat.getColor(this, colourRes)
            val swatch = ItemKitSwatchBinding.inflate(layoutInflater, grid, false)
            swatch.colour.background = GradientDrawable().apply {
                cornerRadius = 16 * dp
                setColor(colour)
                setStroke(dp.roundToInt(), ContextCompat.getColor(this@StyleKitActivity, R.color.line))
            }
            swatch.name.text = name
            swatch.hex.text = String.format(Locale.ROOT, "#%06X", colour and 0xFFFFFF)
            val params = GridLayout.LayoutParams(GridLayout.spec(i / 3), GridLayout.spec(i % 3)).apply {
                width = (120 * dp).roundToInt()
                if (i >= 3) topMargin = (28 * dp).roundToInt()
                if (i % 3 < 2) marginEnd = (6 * dp).roundToInt()
            }
            grid.addView(swatch.root, params)
        }
    }

    /** Each type style's name and size, then its sample set in it. */
    private fun addTypeSamples() {
        val names = resources.getStringArray(R.array.kit_type_names)
        val samples = resources.getStringArray(R.array.kit_type_samples)
        TYPE.forEachIndexed { i, appearance ->
            val row = ItemKitTypeBinding.inflate(layoutInflater, binding.type, true)
            row.name.text = names[i]
            TextViewCompat.setTextAppearance(row.sample, appearance)
            row.sample.text = samples[i]
        }
    }

    private fun bindParts() = with(binding) {
        steps.bind(2, getString(R.string.kit_step_title))

        crestXl.crest("MAR")
        crestL.crest("POT")
        crestM.crest("RAW")
        crestS.crest("CAP")
        avatarXl.avatar(getString(R.string.kit_avatar_xl))
        avatarL.avatar(getString(R.string.kit_avatar_l))
        avatarM.avatar(getString(R.string.kit_avatar_m))
        avatarS.avatar(getString(R.string.kit_avatar_s))

        scoreboard.bind(
            Scoreboard(
                home = "MAR", homeName = getString(R.string.kit_scoreboard_home),
                away = "RAW", awayName = getString(R.string.kit_scoreboard_away),
                state = ScoreState.LIVE, status = getString(R.string.kit_status_live), homeScore = 2, awayScore = 1,
            ),
        )
        clock.text = MatchClock.text(elapsedSeconds = 1230, periodEndMinute = 20)
        // The Figma's dropdown shows its chosen value; false keeps the list closed.
        provinceInput.setText(resources.getStringArray(R.array.kit_provinces).first(), false)
        tieDecided.bind(Tie("MAR", "CAP", scoreA = 2, scoreB = 2, note = getString(R.string.kit_tie_pens), winner = "MAR"))
        tieToPlay.bind(Tie("SAD", "RAW", note = getString(R.string.kit_tie_date)))

        topTab.title.setText(R.string.kit_top_standings)
        topBack.title.setText(R.string.kit_top_edit_profile)
        topBack.action.isVisible = true
        topBack.action.contentDescription = getString(R.string.kit_cd_more)
        // The kit's bottom bar sits mid-page, so it doesn't pad itself by the navigation bar.
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav.root) { _, insets -> insets }
        bottomNav.root.selectedItemId = R.id.nav_home

        sheetUnder.title.setText(R.string.kit_top_edit_profile)
        sheetInPlace.bindKitSheet()
        sheetCell.setOnClickListener { openKitSheet() }
        sheetInPlace.head.sheetClose.setOnClickListener { openKitSheet() }

        emptyState.bind(
            R.drawable.ic_search_off, getString(R.string.kit_empty_title), getString(R.string.kit_empty_body),
            getString(R.string.kit_empty_button),
        )
        banner.label.setText(R.string.kit_banner_label)
        banner.label.isVisible = true
        push.head.text = getString(R.string.kit_push_head, getString(R.string.kit_push_time))
        push.title.setText(R.string.kit_push_title)
        push.body.setText(R.string.kit_push_body)

        // The guide's cells clip what they hold to their 20dp corners.
        listOf(sheetCell, scoreboardCell).forEach { it.clipToOutline = true }
    }

    private fun openKitSheet() {
        val sheet = SheetStyleKitBinding.inflate(layoutInflater)
        sheet.bindKitSheet()
        showSheet(sheet.root)
    }

    private fun SheetStyleKitBinding.bindKitSheet() {
        head.sheetTitle.setText(R.string.kit_sheet_title)
        takePhoto.bind(R.drawable.ic_photo_camera, getString(R.string.kit_sheet_take_photo))
        choosePhoto.bind(R.drawable.ic_photo_library, getString(R.string.kit_sheet_gallery))
        removePhoto.bind(R.drawable.ic_delete, getString(R.string.kit_sheet_remove_photo), danger = true)
    }

    private companion object {
        /** The style guide's colours, in its order (style-guide.js COLOURS). */
        val COLOURS = listOf(
            R.color.blue, R.color.ink, R.color.muted, R.color.line, R.color.tint, R.color.page, R.color.paper,
            R.color.orange, R.color.orange_text, R.color.on_blue_muted, R.color.success, R.color.waiting,
            R.color.waiting_bg, R.color.error, R.color.error_bg, R.color.card_yellow, R.color.card_red,
        )

        /** The team colours, in the style guide's order (TEAM_COLOURS). */
        val TEAM_COLOURS = listOf(
            R.color.team_cap, R.color.team_mar, R.color.team_pot, R.color.team_raw,
            R.color.team_sad, R.color.team_khy, R.color.team_pin, R.color.team_koh,
        )

        /** The 15 type styles, in the style guide's order (TYPE). */
        val TYPE = listOf(
            R.style.TextAppearance_LeagueX_Display_Wordmark, R.style.TextAppearance_LeagueX_Display_Hero,
            R.style.TextAppearance_LeagueX_Display_Clock, R.style.TextAppearance_LeagueX_Display_Score,
            R.style.TextAppearance_LeagueX_Display_Title, R.style.TextAppearance_LeagueX_Display_Small,
            R.style.TextAppearance_LeagueX_Title_Large, R.style.TextAppearance_LeagueX_Title_Medium,
            R.style.TextAppearance_LeagueX_Title_Small, R.style.TextAppearance_LeagueX_Body_Large,
            R.style.TextAppearance_LeagueX_Body_Medium, R.style.TextAppearance_LeagueX_Body_Small,
            R.style.TextAppearance_LeagueX_Label_Large, R.style.TextAppearance_LeagueX_Label_Medium,
            R.style.TextAppearance_LeagueX_Label_Bold,
        )
    }
}
