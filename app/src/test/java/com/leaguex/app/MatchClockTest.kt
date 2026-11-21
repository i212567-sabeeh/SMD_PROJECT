package com.leaguex.app

import com.leaguex.app.kit.MatchClock
import org.junit.Assert.assertEquals
import org.junit.Test

/** The match clock reads mm:ss up to the end of the period, then counts added minutes from "+1". */
class MatchClockTest {

    @Test
    fun countsUpToTheEndOfThePeriod() {
        assertEquals("20:00", MatchClock.text(1200, 20))
        assertEquals("43:10", MatchClock.text(2590, 45))
        assertEquals("33:05", MatchClock.text(1985, 40))
    }

    @Test
    fun countsAddedMinutesAfterIt() {
        assertEquals("20+1", MatchClock.text(1230, 20))
        assertEquals("20+2", MatchClock.text(1260, 20))
        assertEquals("20+3", MatchClock.text(1321, 20))
    }
}
