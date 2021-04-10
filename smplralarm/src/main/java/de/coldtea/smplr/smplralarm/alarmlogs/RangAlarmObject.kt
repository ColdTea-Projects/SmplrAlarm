package de.coldtea.smplr.smplralarm.alarmlogs

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RangAlarmObject(
    val dateTime: String,
    val errorReport: String
)