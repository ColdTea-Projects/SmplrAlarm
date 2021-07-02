package de.coldtea.smplr.smplralarm.extensions

import com.squareup.moshi.Moshi
import de.coldtea.smplr.smplralarm.models.ActiveAlarmList
import de.coldtea.smplr.smplralarm.models.ActiveWeekDays
import de.coldtea.smplr.smplralarm.models.WeekDays
import de.coldtea.smplr.smplralarm.repository.entity.AlarmNotificationEntity

/**
 * Created by [Yasar Naci Gündüz](https://github.com/ColdTea-Projects).
 */

internal fun List<WeekDays>.activeDaysAsJsonString(): String =
    Moshi
        .Builder()
        .build()
        .adapter(ActiveWeekDays::class.java)
        .toJson(
            ActiveWeekDays(this)
        )

internal fun AlarmNotificationEntity.activeDaysAsWeekdaysList(): List<WeekDays>? =
    if (this.weekDays == "[]") listOf()
    else
        Moshi
            .Builder()
            .build()
            .adapter(ActiveWeekDays::class.java)
            .fromJson(this.weekDays)
            ?.days

internal fun ActiveAlarmList.alarmsAsJsonString(): String? =
    Moshi.Builder()
        .build()
        .adapter(ActiveAlarmList::class.java)
        .toJson(this)

internal fun List<Pair<String, String>>?.convertToJson(): String =
    "{${this?.joinToString(separator = ",") { "\"${it.first}\" : \"${it.second}\"" }.orEmpty()}}"


