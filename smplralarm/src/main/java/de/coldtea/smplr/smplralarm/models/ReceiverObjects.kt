package de.coldtea.smplr.smplralarm.receivers

import android.app.NotificationChannelGroup
import android.content.Intent
import androidx.annotation.DrawableRes
import de.coldtea.smplr.smplralarm.managers.AlarmRingEvent
import de.coldtea.smplr.smplralarm.models.NotificationChannelItem
import de.coldtea.smplr.smplralarm.models.NotificationItem
import de.coldtea.smplr.smplralarm.repository.entity.AlarmNotificationEntity
import de.coldtea.smplr.smplralarm.repository.entity.NotificationChannelEntity
import de.coldtea.smplr.smplralarm.repository.entity.NotificationEntity

internal class SmplrAlarmReceiverObjects {
    companion object {
        const val SMPLR_ALARM_RECEIVER_INTENT_ID = "smplr_alarm_receiver_intent_id"
        var alarmNotification: MutableList<AlarmNotification> = mutableListOf()
    }
}

data class AlarmNotification(
    val alarmNotificationId: Int,
    val notificationChannelItem: NotificationChannelItem,
    val notificationItem: NotificationItem,
    val intent: Intent?,
    val fullScreenIntent: Intent?,
    val alarmRingEvent: AlarmRingEvent?
)

fun AlarmNotification.extractAlarmNotificationEntity(): AlarmNotificationEntity =
    AlarmNotificationEntity(
        alarmNotificationId
    )

fun AlarmNotification.extractNotificationEntity(fkAlarmNotificationId: Int): NotificationEntity =
    NotificationEntity(
        0,
        fkAlarmNotificationId,
        notificationItem.smallIcon,
        notificationItem.title,
        notificationItem.message,
        notificationItem.bigText,
        notificationItem.autoCancel
    )

fun AlarmNotification.extractNotificationChannelEntity(fkAlarmNotificationId: Int): NotificationChannelEntity =
    NotificationChannelEntity(
        0,
        fkAlarmNotificationId,
        notificationChannelItem.importance,
        notificationChannelItem.showBadge,
        notificationChannelItem.name,
        notificationChannelItem.description
    )