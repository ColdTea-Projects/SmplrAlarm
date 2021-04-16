package de.coldtea.smplr.smplralarm

import android.content.Context
import de.coldtea.smplr.smplralarm.apis.AlarmNotificationAPI
import de.coldtea.smplr.smplralarm.apis.ChannelManagerAPI
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmListRequestAPI
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmManager
import de.coldtea.smplr.smplralarm.models.NotificationChannelItem
import de.coldtea.smplr.smplralarm.models.NotificationItem


fun smplrAlarmSet(context: Context, lambda: SmplrAlarmManager.() -> Unit): Int =
    SmplrAlarmManager(context).apply(lambda).setAlarm()

fun smplrAlarmCancel(context: Context, lambda: SmplrAlarmManager.() -> Unit) =
    SmplrAlarmManager(context).apply(lambda).removeAlarm()

fun smplrAlarmUpdateRepeatingAlarm(context: Context, lambda: SmplrAlarmManager.() -> Unit) =
    SmplrAlarmManager(context).apply(lambda).updateRepeatingAlarm()

fun smplrAlarmUpdateSingleAlarm(context: Context, lambda: SmplrAlarmManager.() -> Unit) =
    SmplrAlarmManager(context).apply(lambda).updateSingleAlarm()

fun smplrAlarmChangeOrRequestListener(context: Context, lambda:  ((String) -> Unit)) =
    SmplrAlarmListRequestAPI(context).apply {
        alarmListChangeOrRequestedListener = lambda
    }

fun channel(lambda: ChannelManagerAPI.() -> Unit): NotificationChannelItem =
    ChannelManagerAPI().apply(lambda).build()

fun alarmNotification(lamda: AlarmNotificationAPI.() -> Unit): NotificationItem =
    AlarmNotificationAPI().apply(lamda).build()