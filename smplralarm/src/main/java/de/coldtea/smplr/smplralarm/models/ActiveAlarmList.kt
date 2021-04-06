package de.coldtea.smplr.smplralarm.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class ActiveAlarmList(
    val alarmItems: List<AlarmItem>
)