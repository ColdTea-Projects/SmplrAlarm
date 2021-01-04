package de.coldtea.smplr.smplralarm.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.coldtea.smplr.smplralarm.models.NotificationChannelItem

@Entity(tableName = "notification_channel_table")
data class NotificationChannelEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "channel_id")
    val channelId: Int,
    @ColumnInfo(name = "fk_alarm_notification_id")
    val fkAlarmNotificationId: Int,
    @ColumnInfo(name = "importance")
    val importance: Int,
    @ColumnInfo(name = "show_badge")
    val showBadge: Boolean,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String
)

fun NotificationChannelEntity.convertToNotificationChannelItem() =
    NotificationChannelItem(
        importance,
        showBadge,
        name,
        description
    )