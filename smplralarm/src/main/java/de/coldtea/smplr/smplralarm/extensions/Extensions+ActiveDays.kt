package de.coldtea.smplr.smplralarm.extensions

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import de.coldtea.smplr.smplralarm.models.ActiveWeekDays
import de.coldtea.smplr.smplralarm.models.WeekDays

fun List<WeekDays>.activeDaysAsJsonString(): String =
    Moshi
        .Builder()
        .build()
        .adapter(ActiveWeekDays::class.java)
        .toJson(
            ActiveWeekDays(this)
        )

fun String.activeDaysAsWeekdaysList(): List<WeekDays>? =
    Moshi
        .Builder()
        .build()
        .adapter(ActiveWeekDays::class.java)
        .fromJson(this)
        ?.days