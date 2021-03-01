package de.coldtea.smplr.smplralarm.extensions

import de.coldtea.smplr.smplralarm.models.WeekDays
import timber.log.Timber
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.util.Calendar

internal fun Calendar.getTimeExactForAlarmInMiliseconds(
    hour: Int,
    minute: Int,
    weekDays: List<WeekDays>,
    daysToSkip: Int
): Long {
    return getTimeExactForAlarm(hour, minute, weekDays, daysToSkip).timeInMillis
}

private fun Calendar.getTimeExactForAlarm(
    hour: Int,
    minute: Int,
    weekDays: List<WeekDays>,
    daysToSkip: Int
): Calendar {
    timeInMillis = System.currentTimeMillis()

    set(Calendar.HOUR_OF_DAY, hour)
    set(Calendar.MINUTE, minute)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)

    if (weekDays.isNotEmpty()) setTheDay(weekDays.getClosestDay(daysToSkip), isTimeAhead(hour, minute))

    if (timeInMillis <= System.currentTimeMillis()) {
        set(Calendar.DAY_OF_MONTH, get(Calendar.DAY_OF_MONTH) + 1)
    }

    return this
}

private fun Calendar.setTheDay(nextWeekDay: Int, isTimeAhead: Boolean) {
    if((get(Calendar.DAY_OF_WEEK) == nextWeekDay && isTimeAhead)) return

    if (get(Calendar.DAY_OF_WEEK) < nextWeekDay) {
        set(Calendar.DAY_OF_WEEK, nextWeekDay)
        return
    }

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val temporalDayOfWeek = when (nextWeekDay) {
            0 -> DayOfWeek.SUNDAY
            1 -> DayOfWeek.MONDAY
            2 -> DayOfWeek.TUESDAY
            3 -> DayOfWeek.WEDNESDAY
            4 -> DayOfWeek.THURSDAY
            5 -> DayOfWeek.FRIDAY
            6 -> DayOfWeek.SATURDAY
            else -> null
        }

        if (temporalDayOfWeek == null) {
            Timber.e("SmplrAlarm -> The day of week could not be set!")
            return
        }

        val localDate = this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            .with(TemporalAdjusters.next(temporalDayOfWeek))
        set(Calendar.DAY_OF_YEAR, localDate.dayOfYear)
    } else {
        set(Calendar.DAY_OF_MONTH, time.date + (nextWeekDay + 7 - time.day) % 7)
    }
}

private fun Calendar.isTimeAhead(hour: Int, minute: Int) = get(Calendar.HOUR_OF_DAY) < hour || (get(Calendar.HOUR_OF_DAY) == hour && get(Calendar.MINUTE) < minute)

private fun List<WeekDays>.getClosestDay(daysToSkip: Int): Int =
    this.map { it.ordinal }
        .firstOrNull {
            val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1
            it >= today + daysToSkip
        }
        ?: this.first().ordinal



