package de.coldtea.smplr.smplralarm.repository.relations

import androidx.room.Embedded
import androidx.room.Relation
import de.coldtea.smplr.smplralarm.repository.entity.AlarmNotificationEntity
import de.coldtea.smplr.smplralarm.repository.entity.NotificationChannelEntity
import de.coldtea.smplr.smplralarm.repository.entity.NotificationEntity

/**
 * Created by [Yasar Naci Gündüz](https://github.com/ColdTea-Projects).
 */
internal data class AlarmNotifications (
    @Embedded val alarmNotificationEntity: AlarmNotificationEntity,
    @Relation(
        parentColumn = "alarm_notification_id",
        entityColumn = "fk_alarm_notification_id"
    )
    val notificationChannelEntity: NotificationChannelEntity,
    @Relation(
        parentColumn = "alarm_notification_id",
        entityColumn = "fk_alarm_notification_id"
    )
    val notificationEntity: NotificationEntity
)