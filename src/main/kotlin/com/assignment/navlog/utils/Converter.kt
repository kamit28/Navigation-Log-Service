package com.assignment.navlog.utils

import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun toInt(value: String): Int {
    val trimmed = value.trimStart('0')
    return if (trimmed.isEmpty()) {
        return 0
    } else {
        trimmed.toDouble().toInt()
    }
}

fun toDouble(value: String): Double {
    val trimmed = value.trimStart('0')
    return if (trimmed.isEmpty()) {
        return 0.0
    } else {
        trimmed.toDouble()
    }
}

fun toMinutes(value: String): Int {
    val tokens =  value.split(".")
    return toInt(tokens[0])*60 + toInt(tokens[1])
}

fun toISO8601Duration(value: String): String {
    val millis = (toMinutes(value)*60*1000).toLong()
    return millis.toDuration(DurationUnit.MILLISECONDS).toIsoString()
}
