package de.coldtea.smplr.alarm.alarms.models

data class AlarmInfo(
    val requestCode: Int,
    val time: Pair<Int, Int>
)
