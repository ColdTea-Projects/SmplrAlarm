package de.coldtea.smplr.smplralarm.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class AlarmItem(
    val requestId: Int,
    val hour: Int,
    val minute: Int,
    val weekDays: List<WeekDays>
)