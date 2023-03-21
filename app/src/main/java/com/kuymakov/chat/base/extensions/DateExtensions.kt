package com.kuymakov.chat.base.extensions

import android.content.Context
import com.kuymakov.chat.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

fun Date.lastSeen(context: Context): String {
    val lastSeen = context.resources.getString(R.string.user_lastSeen)
    val today = context.resources.getString(R.string.date_day_today)
    val yesterday = context.resources.getString(R.string.date_day_yesterday)
    val now = context.resources.getString(R.string.date_time_now)
    val at = context.resources.getString(R.string.date_time_at)
    val ago = context.resources.getString(R.string.date_time_ago)

    val calendar = Calendar.getInstance()
    calendar.time = this
    val curCalendar = Calendar.getInstance()

    val time = "$at ${format("hh:mm")}"

    val date = if (curCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
        val day = calendar.get(Calendar.DAY_OF_YEAR)
        val curDay = curCalendar.get(Calendar.DAY_OF_YEAR)

        if (curDay == day) {
            val hour = calendar.get(Calendar.HOUR)
            val curHour = curCalendar.get(Calendar.HOUR)

            if (curHour == hour) {
                val minute = calendar.get(Calendar.MINUTE)
                val curMinute = curCalendar.get(Calendar.MINUTE)

                if (curMinute - minute < 1) {
                    now
                } else {
                    "${curMinute - minute}m $ago"
                }
            } else if (curHour - hour < 3) {
                "${curHour - hour}h $ago"
            } else {
                "$today $time"
            }
        } else if (curDay - day == 1) {
            "$yesterday $time"
        } else {
            "${format("dd MMM")} $time"
        }
    } else {
        "${format("dd MMM yyyy")} $time"
    }

    return "$lastSeen $date"
}

fun Date.format(format: String): String {
    val locale = Locale.US
    return SimpleDateFormat(format, locale).format(this)
}


fun LocalDateTime.format(format: String): String {
    return format(DateTimeFormatter.ofPattern(format))
}

fun LocalDateTime.toDate() : Date {
    return Date.from(atZone(ZoneId.systemDefault()).toInstant())
}
fun Date.toLocalDateTime() : LocalDateTime {
    return toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
}

fun LocalDate.format(format: String): String {
    return format(DateTimeFormatter.ofPattern(format))
}
