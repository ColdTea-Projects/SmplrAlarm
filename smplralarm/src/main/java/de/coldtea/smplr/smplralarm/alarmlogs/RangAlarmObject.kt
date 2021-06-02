package de.coldtea.smplr.smplralarm.alarmlogs

import com.squareup.moshi.JsonClass


/**
 * Created by [Yasar Naci Gündüz](https://github.com/ColdTea-Projects).
 */
@JsonClass(generateAdapter = true)
data class RangAlarmObject(
    val dateTime: String,
    val errorReport: String
)