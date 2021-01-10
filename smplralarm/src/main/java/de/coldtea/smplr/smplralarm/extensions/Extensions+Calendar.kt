package de.coldtea.smplr.smplralarm.extensions

import java.util.Calendar

internal fun Calendar.getTimeExactForAlarmInMiliseconds(hour: Int, minute: Int): Long{
    return getTimeExactForAlarm(hour, minute).timeInMillis
}

internal fun Calendar.getTimeExactForAlarm(hour: Int, minute: Int): Calendar{
    timeInMillis = System.currentTimeMillis()
    set(Calendar.HOUR_OF_DAY, hour)
    set(Calendar.MINUTE, minute)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)

    if (timeInMillis <= System.currentTimeMillis()) {
        set(Calendar.DAY_OF_MONTH, get(Calendar.DAY_OF_MONTH) + 1)
    }

    return this
}