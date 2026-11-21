package com.leaguex.app

import com.leaguex.app.matchday.Shootout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** 50 Penalty shootout's rules: five kicks each, ended early once a team can't be caught, then sudden death. */
class ShootoutTest {

    private val o = true
    private val x = false

    @Test
    fun goesOnWhileTheTrailingTeamCanStillCatchUp() {
        // The Figma: Margalla 4 from 5, Capital 3 from 4, with Capital's 5th kick to take.
        assertNull(Shootout.winner(listOf(o, o, x, o, o), listOf(o, x, o, o)))
        assertNull(Shootout.winner(listOf(o, o, o), listOf(x, x)))
    }

    @Test
    fun endsOnceOneTeamCantBeCaught() {
        assertEquals("MAR", Shootout.winner(listOf(o, o, x, o, o), listOf(o, x, o, o, x)))
        assertEquals("MAR", Shootout.winner(listOf(o, o, o), listOf(x, x, x)))
        assertEquals("CAP", Shootout.winner(listOf(x, x, x), listOf(o, o, o)))
    }

    @Test
    fun levelAfterFiveGoesToSuddenDeath() {
        assertNull(Shootout.winner(listOf(o, o, x, o, o), listOf(o, x, o, o, o)))
        assertNull(Shootout.winner(listOf(o, o, x, o, o, o), listOf(o, x, o, o, o)))
        assertNull(Shootout.winner(listOf(o, o, x, o, o, o), listOf(o, x, o, o, o, o)))
        assertEquals("MAR", Shootout.winner(listOf(o, o, x, o, o, o), listOf(o, x, o, o, o, x)))
        assertEquals("CAP", Shootout.winner(listOf(o, o, x, o, o, x), listOf(o, x, o, o, o, o)))
    }

    @Test
    fun namesKicksInOrder() {
        assertEquals(
            listOf("1st", "2nd", "3rd", "4th", "5th", "11th", "12th", "13th", "21st", "22nd"),
            listOf(1, 2, 3, 4, 5, 11, 12, 13, 21, 22).map(Shootout::ordinal),
        )
    }
}
