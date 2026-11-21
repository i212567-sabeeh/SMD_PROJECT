package com.leaguex.app.kit

import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import com.leaguex.app.data.SampleData
import com.leaguex.app.data.SquadPlayer
import com.leaguex.app.databinding.IncludeInfoCardBinding
import com.leaguex.app.databinding.IncludeLineupsBinding
import com.leaguex.app.databinding.ItemLineupPlayerBinding

/**
 * A card with [icon] in a tint circle, [title] and an optional [meta] line (card pad row-x gap-12). With [onClick] it
 * shows a chevron and is tappable; without, it's plain.
 */
fun IncludeInfoCardBinding.bind(@DrawableRes icon: Int, title: CharSequence, meta: CharSequence?, onClick: (() -> Unit)? = null) {
    this.icon.setImageResource(icon)
    this.title.text = title
    this.meta.text = meta
    this.meta.isVisible = meta != null
    chevron.isVisible = onClick != null
    if (onClick != null) {
        root.setOnClickListener { onClick() }
    } else {
        root.setOnClickListener(null)
        root.isClickable = false
        root.foreground = null
    }
}

/** Both teams' line-ups side by side (14 Match page): each team's crest and name, then its players' numbers and names. */
fun IncludeLineupsBinding.bind(home: String, homePlayers: List<SquadPlayer>, away: String, awayPlayers: List<SquadPlayer>) {
    homeCrest.crest(home)
    homeName.text = SampleData.teamName(home)
    awayCrest.crest(away)
    awayName.text = SampleData.teamName(away)
    fill(this.home, homePlayers)
    fill(this.away, awayPlayers)
}

private fun fill(column: LinearLayout, players: List<SquadPlayer>) {
    while (column.childCount > 1) column.removeViewAt(1)
    val inflater = LayoutInflater.from(column.context)
    for (p in players) {
        val row = ItemLineupPlayerBinding.inflate(inflater, column, true)
        row.number.text = p.number.toString()
        row.name.text = p.name
    }
}
