package de.coldtea.smplr.smplralarm

import android.content.Context
import de.coldtea.smplr.smplralarm.managers.SmplrAlarmListRequestManager
import de.coldtea.smplr.smplralarm.managers.SmplrAlarmManager


fun smplrAlarmSet(context: Context, lambda: SmplrAlarmManager.() -> Unit): Int =
    SmplrAlarmManager(context).apply(lambda).setAlarm()

fun smplrAlarmCancel(context: Context, lambda: SmplrAlarmManager.() -> Unit) =
    SmplrAlarmManager(context).apply(lambda).removeAlarm()

fun smplrAlarmUpdateRepeatingAlarm(context: Context, lambda: SmplrAlarmManager.() -> Unit) =
    SmplrAlarmManager(context).apply(lambda).updateRepeatingAlarm()

fun smplrAlarmUpdateSingleAlarm(context: Context, lambda: SmplrAlarmManager.() -> Unit) =
    SmplrAlarmManager(context).apply(lambda).updateSingleAlarm()

fun smplrAlarmChangeOrRequestListener(context: Context, lambda:  ((String) -> Unit)) =
    SmplrAlarmListRequestManager(context).apply {
        alarmListChangeOrRequestedListener = lambda
    }