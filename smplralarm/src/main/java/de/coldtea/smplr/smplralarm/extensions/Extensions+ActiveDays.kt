package de.coldtea.smplr.smplralarm.extensions

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import de.coldtea.smplr.smplralarm.models.ActiveWeekDays
import de.coldtea.smplr.smplralarm.models.WeekDays
import de.coldtea.smplr.smplralarm.repository.entity.AlarmNotificationEntity

fun List<WeekDays>.activeDaysAsJsonString(): String =
    Moshi
        .Builder()
        .build()
        .adapter(ActiveWeekDays::class.java)
        .toJson(
            ActiveWeekDays(this)
        )

fun AlarmNotificationEntity.activeDaysAsWeekdaysList(): List<WeekDays>? =
    Moshi
        .Builder()
        .build()
        .adapter(ActiveWeekDays::class.java)
        .fromJson(this.weekDays)
        ?.days