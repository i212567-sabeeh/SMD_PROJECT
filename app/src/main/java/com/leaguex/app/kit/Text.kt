package com.leaguex.app.kit

private val TIME = Regex("(\\d:\\d\\d) (am|pm)\\b")
private val DAY = Regex("\\b(Mon|Tue|Wed|Thu|Fri|Sat|Sun) (\\d{1,2})\\b")
private val DATE = Regex("\\b(\\d{1,2}) (Oct|Nov|Dec)\\b")

/**
 * Joins times ("7:00 pm") and dates ("Sat 15 Nov") with non-breaking spaces so they never split across lines,
 * as ui.js keepTogether() does.
 */
fun keepTogether(text: String): String = text
    .replace(TIME, "$1\u00A0$2")
    .replace(DAY, "$1\u00A0$2")
    .replace(DATE, "$1\u00A0$2")
