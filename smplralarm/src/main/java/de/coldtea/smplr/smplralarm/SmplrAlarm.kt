package de.coldtea.smplr.smplralarm

import android.content.Context
import de.coldtea.smplr.smplralarm.apis.AlarmNotificationAPI
import de.coldtea.smplr.smplralarm.apis.ChannelManagerAPI
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmAPI
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmListRequestAPI
import de.coldtea.smplr.smplralarm.models.NotificationChannelItem
import de.coldtea.smplr.smplralarm.models.NotificationItem


fun smplrAlarmSet(context: Context, lambda: SmplrAlarmAPI.() -> Unit): Int =
    SmplrAlarmAPI(context).apply(lambda).setAlarm()

fun smplrAlarmCancel(context: Context, lambda: SmplrAlarmAPI.() -> Unit) =
    SmplrAlarmAPI(context).apply(lambda).removeAlarm()

fun smplrAlarmRenewMissingAlarms(context: Context) =
    SmplrAlarmAPI(context).renewMissingAlarms()

fun smplrAlarmUpdate(context: Context, lambda: SmplrAlarmAPI.() -> Unit) =
    SmplrAlarmAPI(context).apply(lambda).updateAlarm()

fun smplrAlarmChangeOrRequestListener(context: Context, lambda:  ((String) -> Unit)) =
    SmplrAlarmListRequestAPI(context).apply {
        alarmListChangeOrRequestedListener = lambda
    }

fun channel(lambda: ChannelManagerAPI.() -> Unit): NotificationChannelItem =
    ChannelManagerAPI().apply(lambda).build()

fun alarmNotification(lamda: AlarmNotificationAPI.() -> Unit): NotificationItem =
    AlarmNotificationAPI().apply(lamda).build()