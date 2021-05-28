package de.coldtea.smplr.smplralarm.receivers

import android.content.Intent
import de.coldtea.smplr.smplralarm.apis.AlarmRingEvent
import de.coldtea.smplr.smplralarm.extensions.activeDaysAsJsonString
import de.coldtea.smplr.smplralarm.models.NotificationChannelItem
import de.coldtea.smplr.smplralarm.models.NotificationItem
import de.coldtea.smplr.smplralarm.models.WeekDays
import de.coldtea.smplr.smplralarm.repository.entity.AlarmNotificationEntity
import de.coldtea.smplr.smplralarm.repository.entity.NotificationChannelEntity
import de.coldtea.smplr.smplralarm.repository.entity.NotificationEntity

internal class SmplrAlarmReceiverObjects {
    companion object {
        internal const val SMPLR_ALARM_RECEIVER_INTENT_ID = "smplr_alarm_receiver_intent_id"

        internal var alarmNotification: MutableList<AlarmNotification> = mutableListOf()
    }
}

data class AlarmNotification(
    val alarmNotificationId: Int,
    val hour: Int,
    val min: Int,
    val weekDays: List<WeekDays>,
    val notificationChannelItem: NotificationChannelItem,
    val notificationItem: NotificationItem,
    val intent: Intent?,
    val fullScreenIntent: Intent?,
    val isActive: Boolean,
    val alarmRingEvent: AlarmRingEvent?
)

internal fun AlarmNotification.extractAlarmNotificationEntity(): AlarmNotificationEntity =
    AlarmNotificationEntity(
        alarmNotificationId,
        hour,
        min,
        weekDays.activeDaysAsJsonString(),
        isActive
    )

internal fun AlarmNotification.extractNotificationEntity(fkAlarmNotificationId: Int): NotificationEntity =
    NotificationEntity(
        0,
        fkAlarmNotificationId,
        notificationItem.smallIcon,
        notificationItem.title,
        notificationItem.message,
        notificationItem.bigText,
        notificationItem.autoCancel,
        notificationItem.firstButtonText.orEmpty(),
        notificationItem.secondButtonText.orEmpty()
    )

internal fun AlarmNotification.extractNotificationChannelEntity(fkAlarmNotificationId: Int): NotificationChannelEntity =
    NotificationChannelEntity(
        0,
        fkAlarmNotificationId,
        notificationChannelItem.importance,
        notificationChannelItem.showBadge,
        notificationChannelItem.name,
        notificationChannelItem.description
    )