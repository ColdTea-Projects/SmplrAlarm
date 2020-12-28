package de.coldtea.smplr.smplralarm

import android.content.Context

fun smplrAlarmSet(context: Context, lambda: SmplrAlarmManager.() -> Unit):Int =
    SmplrAlarmManager(context).apply(lambda).setAlarm()

fun smplrAlarmCancel(context: Context, lambda: SmplrAlarmManager.() -> Unit) =
    SmplrAlarmManager(context).apply(lambda).cancelAlarm()