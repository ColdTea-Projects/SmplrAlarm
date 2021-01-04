package de.coldtea.smplr.smplralarm.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.coldtea.smplr.smplralarm.models.NotificationItem

@Entity(tableName = "notification_table")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "full_screen_intent_id")
    val notificationId: Int,
    @ColumnInfo(name = "fk_alarm_notification_id")
    val fkAlarmNotificationId: Int,
    @ColumnInfo(name = "small_icon")
    val smallIcon: Int,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "message")
    val message: String,
    @ColumnInfo(name = "big_text")
    val bigText: String,
    @ColumnInfo(name = "auto_cancel")
    val autoCancel: Boolean
)

fun NotificationEntity.convertToNotificationItem() =
    NotificationItem(
        smallIcon,
        title,
        message,
        bigText,
        autoCancel
    )