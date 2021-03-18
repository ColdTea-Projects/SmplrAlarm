package de.coldtea.smplr.smplralarm.alarmlogs

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RangAlarmObject(
    val date: String,
    val time: String
)