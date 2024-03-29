package de.coldtea.smplr.smplralarm.models

import com.squareup.moshi.JsonClass

/**
 * Created by [Yasar Naci Gündüz](https://github.com/ColdTea-Projects).
 */
@JsonClass(generateAdapter = true)
internal data class AlarmItem(
    val requestId: Int,
    val hour: Int,
    val minute: Int,
    val weekDays: List<WeekDays>,
    val isActive: Boolean,
    val infoPairs: String
)