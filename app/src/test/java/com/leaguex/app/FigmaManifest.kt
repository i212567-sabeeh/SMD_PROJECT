package com.leaguex.app

import org.json.JSONArray
import org.json.JSONObject

data class FigmaSection(val slug: String, val title: String, val frames: List<FigmaFrame>)

/** One Figma frame as the HTML build describes it. [top] is "blue" or "light": what the status bar sits on. */
data class FigmaFrame(val id: String, val name: String, val nav: Boolean, val top: String, val expect: List<String>)

/** The Figma frames, from figma-frames.json (design/screens/dist/manifest.json, copied by tools/sync-figma.mjs). */
object FigmaManifest {
    val sections: List<FigmaSection> by lazy { parse(read("figma-frames.json")) }

    private fun read(name: String): String {
        val stream = FigmaManifest::class.java.classLoader?.getResourceAsStream(name)
            ?: error("$name isn't in the test resources. Run: node tools/sync-figma.mjs")
        return stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
    }

    private fun parse(json: String): List<FigmaSection> =
        JSONObject(json).getJSONArray("sections").objects().map { section ->
            FigmaSection(
                slug = section.getString("slug"),
                title = section.getString("title"),
                frames = section.getJSONArray("screens").objects().map { frame ->
                    FigmaFrame(
                        id = frame.getString("id"),
                        name = frame.getString("name"),
                        nav = frame.getBoolean("nav"),
                        top = frame.getString("top"),
                        expect = frame.getJSONArray("expect").strings(),
                    )
                },
            )
        }

    private fun JSONArray.objects(): List<JSONObject> = List(length()) { getJSONObject(it) }
    private fun JSONArray.strings(): List<String> = List(length()) { getString(it) }
}
