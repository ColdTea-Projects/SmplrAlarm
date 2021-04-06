package de.coldtea.smplr.smplralarm.extensions

import de.coldtea.smplr.smplralarm.models.AlarmItem
import de.coldtea.smplr.smplralarm.models.NotificationItem
import de.coldtea.smplr.smplralarm.repository.entity.AlarmNotificationEntity
import de.coldtea.smplr.smplralarm.repository.entity.NotificationEntity

internal fun NotificationEntity.convertToNotificationItem() =
    NotificationItem(
        smallIcon,
        title,
        message,
        bigText,
        autoCancel
    )

internal fun AlarmNotificationEntity.convertToAlarmItem() =
    AlarmItem(
        alarmNotificationId,
        hour,
        min,
        activeDaysAsWeekdaysList().orEmpty(),
        isActive
    )