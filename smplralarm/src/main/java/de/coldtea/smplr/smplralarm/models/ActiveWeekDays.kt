package de.coldtea.smplr.smplralarm.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class ActiveWeekDays(
    val days: List<WeekDays>
)