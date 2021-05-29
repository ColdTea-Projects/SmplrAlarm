package de.coldtea.smplr.smplralarm.models

import com.squareup.moshi.JsonClass

/**
 * Created by [Yasar Naci Gündüz](https://github.com/ColdTea-Projects?tab=following).
 */
@JsonClass(generateAdapter = true)
internal data class ActiveAlarmList(
    val alarmItems: List<AlarmItem>
)