package com.leaguex.app.kit

import java.util.Locale

/** The match clock, as Record match and the live Match page show it. */
object MatchClock {

    /**
     * [elapsedSeconds] of play as "mm:ss", up to and including the end of the period at [periodEndMinute]:00.
     * After that, added time reads "<periodEndMinute>+<n>", counting the first added minute as "+1": 20:30 in a
     * 20-minute half is "20+1".
     */
    fun text(elapsedSeconds: Int, periodEndMinute: Int): String {
        val seconds = elapsedSeconds.coerceAtLeast(0)
        val end = periodEndMinute * 60
        return if (seconds <= end) {
            String.format(Locale.ROOT, "%02d:%02d", seconds / 60, seconds % 60)
        } else {
            "$periodEndMinute+${(seconds - end) / 60 + 1}"
        }
    }

    /** The minute an event at [elapsedSeconds] happens in, as 47–49's Minute field fills it in: "33'", or "20+1'" in added time. */
    fun minute(elapsedSeconds: Int, periodEndMinute: Int): String {
        val seconds = elapsedSeconds.coerceAtLeast(0)
        val end = periodEndMinute * 60
        return if (seconds <= end) "${seconds / 60}'" else "$periodEndMinute+${(seconds - end) / 60 + 1}'"
    }
}
