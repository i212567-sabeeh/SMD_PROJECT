package com.leaguex.app.data

/**
 * The Figma's sample content, copied from design/screens/src/data.js (and the cup semi-final in league.js).
 * People and teams are fictional. Nothing is loaded or saved: the backend comes later.
 */
object SampleData {

    val TEAMS: Map<String, Team> = listOf(
        Team("CAP", "Capital United", "Danish Iqbal"),
        Team("MAR", "Margalla Strikers", "Hamza Ali"),
        Team("POT", "Potohar Lions", "Junaid Akhtar", inkText = true),
        Team("RAW", "Rawal Rovers", "Owais Siddiqui"),
        Team("SAD", "Saddar City", "Rehan Butt"),
        Team("KHY", "Khyber Eagles", "Imran Afridi"),
        Team("PIN", "Pindi Panthers", "Fahad Mirza"),
        Team("KOH", "Kohsar FC", "Shahzaib Khan"),
    ).associateBy { it.code }

    fun teamName(code: String): String = TEAMS[code]?.name ?: code

    /** The Figma's signed-in player: Usman Tariq, #7 winger for Margalla Strikers. */
    val ME = Person(
        name = "Usman Tariq", email = "usman.tariq@example.com", team = "MAR", number = 7, position = "Winger",
        province = "Islamabad Capital Territory", city = "Islamabad",
    )

    /** The league table after matchday 4. */
    val TABLE: List<TableRow> = listOf(
        listOf("CAP", 4, 4, 0, 0, 7, 12), listOf("MAR", 4, 3, 1, 0, 5, 10), listOf("POT", 4, 2, 2, 0, 2, 8),
        listOf("RAW", 4, 2, 1, 1, 1, 7), listOf("SAD", 4, 1, 2, 1, 0, 5), listOf("KHY", 4, 0, 1, 3, -3, 1),
        listOf("PIN", 4, 0, 1, 3, -4, 1), listOf("KOH", 4, 0, 0, 4, -8, 0),
    ).mapIndexed { i, r -> TableRow(i + 1, r[0] as String, r[1] as Int, r[2] as Int, r[3] as Int, r[4] as Int, r[5] as Int, r[6] as Int) }

    val LEAGUE = Competition(
        name = "Islamabad Futsal League", code = "IFL-7K2Q", season = "Season 2026",
        place = "Islamabad, Islamabad Capital Territory", venue = "F-9 Sports Arena", organizer = "Kamran Shah",
    )
    val CUP = Competition(name = "Capital Futsal Cup", code = "CFC-3M8D", season = "2026")

    /** A knockout the organizer opened for entries; team requests and the captain's registration are for this. */
    val WINTER = Competition(name = "Islamabad Winter Cup", code = "IWC-5T9P", starts = "Sat\u00A06\u00A0Dec")

    val MATCHDAY4: List<MatchRow> = listOf(
        MatchRow("CAP", "KHY", MatchState.RESULT, 3, 1), MatchRow("POT", "SAD", MatchState.RESULT, 2, 2),
        MatchRow("MAR", "PIN", MatchState.RESULT, 4, 2), MatchRow("RAW", "KOH", MatchState.RESULT, 3, 1),
    )

    /** The next matchday, and the date it's played. */
    const val NEXT_MATCHDAY = 5
    const val MATCHDAY5_DATE = "Sat\u00A015\u00A0Nov"

    val MATCHDAY5: List<MatchRow> = listOf(
        MatchRow("KHY", "KOH", MatchState.UPCOMING, time = "5:30\u00A0pm", venue = "Court 2"),
        MatchRow("CAP", "POT", MatchState.UPCOMING, time = "6:00\u00A0pm", venue = "Court 1"),
        MatchRow("MAR", "RAW", MatchState.UPCOMING, time = "7:00\u00A0pm", venue = "Court 2"),
        MatchRow("SAD", "PIN", MatchState.UPCOMING, time = "8:00\u00A0pm", venue = "Court 1"),
    )

    val SQUADS: Map<String, List<SquadPlayer>> = mapOf(
        "MAR" to listOf(
            SquadPlayer(1, "Bilal Ahmed", "Goalkeeper"), SquadPlayer(4, "Faizan Malik", "Defender"),
            SquadPlayer(5, "Ali Raza", "Defender"), SquadPlayer(7, "Usman Tariq", "Winger"),
            SquadPlayer(8, "Adnan Sheikh", "Midfielder"), SquadPlayer(9, "Zain Abbas", "Forward"),
            SquadPlayer(10, "Hamza Ali", "Midfielder"), SquadPlayer(11, "Muhammad Abdullah Qureshi", "Forward"),
            SquadPlayer(12, "Hassan Javed", "Goalkeeper"),
        ),
        "CAP" to listOf(
            SquadPlayer(1, "Taimoor Aslam", "Goalkeeper"), SquadPlayer(2, "Bilal Haroon", "Defender"),
            SquadPlayer(5, "Kamil Yousaf", "Defender"), SquadPlayer(7, "Sameer Qadir", "Winger"),
            SquadPlayer(8, "Asad Mehmood", "Midfielder"), SquadPlayer(9, "Danish Iqbal", "Forward"),
            SquadPlayer(10, "Raheel Akbar", "Midfielder"),
        ),
        "RAW" to listOf(
            SquadPlayer(1, "Arsalan Mir", "Goalkeeper"), SquadPlayer(3, "Waqas Anwar", "Defender"),
            SquadPlayer(6, "Shoaib Akram", "Midfielder"), SquadPlayer(9, "Nabeel Haider", "Forward"),
            SquadPlayer(10, "Owais Siddiqui", "Midfielder"), SquadPlayer(11, "Daniyal Rauf", "Winger"),
            SquadPlayer(14, "Adeel Sarwar", "Defender"),
        ),
    )

    /** MAR vs RAW, matchday 5, oldest first. */
    val MAR_RAW: List<MatchEvent> = listOf(
        MatchEvent("6'", EventType.GOAL, Side.HOME, "Zain Abbas", "Assist: Usman Tariq"),
        MatchEvent("14'", EventType.GOAL, Side.AWAY, "Nabeel Haider"),
        MatchEvent(null, EventType.PERIOD, Side.HOME, "Half time, 1–1"),
        MatchEvent("21'", EventType.YELLOW, Side.HOME, "Faizan Malik", "Yellow card"),
        MatchEvent("29'", EventType.GOAL, Side.HOME, "Usman Tariq", "Assist: Hamza Ali"),
        MatchEvent("30'", EventType.SUB, Side.AWAY, "Daniyal Rauf on", "Waqas Anwar off"),
        MatchEvent("35'", EventType.GOAL, Side.AWAY, "Nabeel Haider", "Assist: Owais Siddiqui"),
        MatchEvent("38'", EventType.GOAL, Side.HOME, "Hamza Ali"),
    )

    /** The Capital Futsal Cup semi-final, MAR vs CAP, oldest first (league.js cupEvents). */
    val CUP_SEMI: List<MatchEvent> = listOf(
        MatchEvent("12'", EventType.GOAL, Side.HOME, "Zain Abbas", "Assist: Usman Tariq"),
        MatchEvent("25'", EventType.GOAL, Side.AWAY, "Danish Iqbal"),
        MatchEvent("33'", EventType.GOAL, Side.AWAY, "Danish Iqbal", "Penalty"),
        MatchEvent("38'", EventType.GOAL, Side.HOME, "Hamza Ali"),
    )

    /** The Capital Futsal Cup quarter-finals (08 Home / Tournament); RAW beat KHY on penalties. */
    val CUP_QUARTERS: List<MatchRow> = listOf(
        MatchRow("CAP", "KOH", MatchState.RESULT, 4, 1), MatchRow("MAR", "PIN", MatchState.RESULT, 3, 0),
        MatchRow("SAD", "POT", MatchState.RESULT, 2, 1), MatchRow("RAW", "KHY", MatchState.RESULT, 2, 2, meta = "Pens"),
    )

    /** Matchday 5's early kick-offs, already played on match night. */
    val EARLIER_TODAY: List<MatchRow> = listOf(
        MatchRow("CAP", "POT", MatchState.RESULT, 2, 1), MatchRow("KHY", "KOH", MatchState.RESULT, 1, 1),
    )

    /** Competitions near the player, for someone who follows nothing yet: name and a line about it. */
    val SUGGESTIONS: List<Pair<String, String>> = listOf(
        "Islamabad Futsal League" to "League, 8 teams, Islamabad",
        "Capital Futsal Cup" to "Knockout, 8 teams, Islamabad",
        "Rawalpindi Football League" to "League, 10 teams, Rawalpindi",
    )

    /** Players with no team yet, shown when a captain searches for forwards to invite: name and position. */
    val FREE_AGENTS: List<Pair<String, String>> = listOf(
        "Kashif Mehmood" to "Forward", "Sohail Abbasi" to "Forward", "Taha Nadeem" to "Forward",
    )
}
