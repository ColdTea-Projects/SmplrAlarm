package de.coldtea.smplr.smplralarm.models

import com.squareup.moshi.JsonClass

/**
 * Created by [Yasar Naci Gündüz](https://github.com/ColdTea-Projects).
 */
@JsonClass(generateAdapter = true)
internal data class ActiveWeekDays(
    val days: List<WeekDays>
)