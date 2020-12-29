package de.coldtea.smplr.alarm.extensions

import java.util.*

fun Calendar.nowPlus(minute: Int):Pair<Int, Int>{
    val hour = get(Calendar.HOUR_OF_DAY)
    val min = get(Calendar.MINUTE)

    val hoursToAdd = ((minute + min) / 60)
    val minutesToAdd = (minute + min) % 60

    return (hoursToAdd + hour)%24 to (minutesToAdd)
}