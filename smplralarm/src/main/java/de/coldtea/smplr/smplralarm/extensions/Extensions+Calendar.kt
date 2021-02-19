package de.coldtea.smplr.smplralarm.extensions

import de.coldtea.smplr.smplralarm.models.WeekDays
import java.util.Calendar

internal fun Calendar.getTimeExactForAlarmInMiliseconds(hour: Int, minute: Int, weekDays: List<WeekDays>): Long{
    return getTimeExactForAlarm(hour, minute, weekDays).timeInMillis
}

internal fun Calendar.getTimeExactForAlarm(hour: Int, minute: Int, weekDays: List<WeekDays>): Calendar{
    timeInMillis = System.currentTimeMillis()

    if (weekDays.isNotEmpty()) set(Calendar.DAY_OF_WEEK, weekDays.getClosestDay())
    set(Calendar.HOUR_OF_DAY, hour)
    set(Calendar.MINUTE, minute)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)

    if (timeInMillis <= System.currentTimeMillis()) {
        set(Calendar.DAY_OF_MONTH, get(Calendar.DAY_OF_MONTH) + 1)
    }

    return this
}

internal fun List<WeekDays>.getClosestDay(): Int =
    this.map { it.ordinal +  1}
        .firstOrNull {
            val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            it > today
        }
        ?:this.first().ordinal + 1
