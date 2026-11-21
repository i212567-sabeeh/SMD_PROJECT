package com.leaguex.app.data

/** What the person has picked while using the app. It lives in memory only, as nothing is saved yet. */
object Session {

    /** The competition picked at the top of Home (09 Switch league): the league, or the Capital Futsal Cup. */
    var competition: Competition = SampleData.LEAGUE

    val cupPicked: Boolean get() = competition == SampleData.CUP
}
