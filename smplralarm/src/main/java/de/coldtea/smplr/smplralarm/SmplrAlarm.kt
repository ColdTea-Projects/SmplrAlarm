package de.coldtea.smplr.smplralarm

import android.content.Context
import de.coldtea.smplr.smplralarm.managers.AlarmManager
import de.coldtea.smplr.smplralarm.managers.AlarmNotificationManager
import de.coldtea.smplr.smplralarm.managers.ChannelManager
import de.coldtea.smplr.smplralarm.models.NotificationChannelItem
import de.coldtea.smplr.smplralarm.models.NotificationItem

fun smplrAlarmSet(context: Context, lambda: AlarmManager.() -> Unit):Int =
    AlarmManager(context).apply(lambda).setAlarm()

fun smplrAlarmCancel(context: Context, lambda: AlarmManager.() -> Unit) =
    AlarmManager(context).apply(lambda).cancelAlarm()

fun channel(lambda: ChannelManager.() -> Unit) : NotificationChannelItem =
    ChannelManager().apply(lambda).build()

fun alarmNotification(lamda: AlarmNotificationManager.() -> Unit) : NotificationItem =
    AlarmNotificationManager().apply(lamda).build()