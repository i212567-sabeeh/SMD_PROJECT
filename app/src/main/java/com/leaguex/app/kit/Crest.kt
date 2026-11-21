package com.leaguex.app.kit

import android.content.res.ColorStateList
import android.graphics.drawable.LayerDrawable
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.leaguex.app.R

/** Each team's colour, by short code (kit.css --team-*). */
private val TEAM_COLOURS = mapOf(
    "CAP" to R.color.team_cap,
    "MAR" to R.color.team_mar,
    "POT" to R.color.team_pot,
    "RAW" to R.color.team_raw,
    "KHY" to R.color.team_khy,
    "SAD" to R.color.team_sad,
    "PIN" to R.color.team_pin,
    "KOH" to R.color.team_koh,
)

/** [code]'s team colour, or the line colour for a team still to be decided. */
@ColorRes
fun teamColour(code: String): Int = TEAM_COLOURS[code] ?: R.color.line

/** Teams whose colour is light enough to need ink text on their crest (data.js `ink: true`). */
private val INK_TEXT = setOf("POT")

/**
 * Makes this TextView [code]'s crest: the short code on the team colour, in ink for Potohar Lions and white for
 * the others. Style the view Widget.LeagueX.Crest.<Size>; with .Ring, only the circle inside the ring is coloured.
 * An unknown code gets the line colour, as a team still to be decided does.
 */
fun TextView.crest(code: String) {
    text = code
    setTextColor(ContextCompat.getColor(context, if (code in INK_TEXT) R.color.ink else R.color.on_blue))
    val colour = ContextCompat.getColor(context, TEAM_COLOURS[code] ?: R.color.line)
    if (background == null) setBackgroundResource(R.drawable.bg_crest)
    val ringed = background as? LayerDrawable
    if (ringed?.findDrawableByLayerId(R.id.crest_fill) != null) {
        // Mutated, so tinting this crest doesn't tint every other ringed crest sharing the drawable.
        ringed.mutate()
        ringed.findDrawableByLayerId(R.id.crest_fill).setTint(colour)
    } else {
        backgroundTintList = ColorStateList.valueOf(colour)
    }
}
