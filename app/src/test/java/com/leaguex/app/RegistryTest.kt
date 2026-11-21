package com.leaguex.app

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.leaguex.app.screens.Frames
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/** The screen list holds every Figma frame, and nothing else, in the Figma's order, and each one opens. */
@RunWith(AndroidJUnit4::class)
class RegistryTest {

    @Test
    fun matchesFigma() {
        val figma = FigmaManifest.sections.flatMap { section -> section.frames.map { it.id to it.name } }
        assertEquals(80, figma.size)
        assertEquals(figma, Frames.all.map { it.id to it.name })
    }

    @Test
    fun everyFrameOpensADeclaredActivity() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val unresolved = Frames.all.filter { app.packageManager.resolveActivity(it.open(app), 0) == null }.map { it.id }
        assertEquals(emptyList<String>(), unresolved)
    }
}
