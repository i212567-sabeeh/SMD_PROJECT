package com.leaguex.app.data

/** A team, by its three-letter code (data.js TEAMS). [inkText] teams have a light colour, so their crest text is ink. */
data class Team(val code: String, val name: String, val captain: String, val inkText: Boolean = false)

/** A league or tournament (data.js LEAGUE, CUP, WINTER). Fields a competition doesn't have are blank. */
data class Competition(
    val name: String,
    val code: String,
    val season: String = "",
    val place: String = "",
    val venue: String = "",
    val organizer: String = "",
    val starts: String = "",
)

/** One line of the league table. [gd] is the goal difference, negative when a team has conceded more. */
data class TableRow(val pos: Int, val code: String, val p: Int, val w: Int, val d: Int, val l: Int, val gd: Int, val pts: Int)

enum class MatchState { UPCOMING, LIVE, RESULT }

/**
 * A match in a list (ui.js matchRow and resultRow). Upcoming matches have a [time] and [venue]; live ones a
 * [minute]; results a final score and an optional [meta] note under "FT" ("Pens 4–3"). [updated] is the text of
 * an "Updated" status chip under the teams, as a live score that's waiting for the organizer shows.
 */
data class MatchRow(
    val home: String,
    val away: String,
    val state: MatchState,
    val homeScore: Int? = null,
    val awayScore: Int? = null,
    val time: String? = null,
    val venue: String? = null,
    val minute: String? = null,
    val meta: String? = null,
    val updated: String? = null,
)

enum class EventType { GOAL, YELLOW, RED, SUB, PERIOD }

enum class Side { HOME, AWAY }

/** A timeline entry (ui.js eventRow). A PERIOD entry is a divider whose text is [player], such as "Half time, 1–1". */
data class MatchEvent(
    val minute: String?,
    val type: EventType,
    val side: Side,
    val player: String,
    val detail: String? = null,
    val waiting: Boolean = false,
)

/** A squad member: shirt number, name and position. */
data class SquadPlayer(val number: Int, val name: String, val position: String)
