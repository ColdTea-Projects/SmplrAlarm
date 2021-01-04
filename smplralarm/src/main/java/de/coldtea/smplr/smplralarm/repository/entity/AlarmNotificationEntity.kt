package de.coldtea.smplr.smplralarm.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm_notification_table")
data class AlarmNotificationEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "alarm_notification_id")
    val alarmNotificationId: Int,
    @ColumnInfo(name = "intent_id")
    val intentId: Int,
    @ColumnInfo(name = "full_screen_intent_id")
    val fullScreenIntentId: Int,
)