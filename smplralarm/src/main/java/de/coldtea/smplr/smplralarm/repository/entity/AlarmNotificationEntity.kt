package de.coldtea.smplr.smplralarm.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm_notification_table")
internal data class AlarmNotificationEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "alarm_notification_id")
    val alarmNotificationId: Int,
    @ColumnInfo(name = "hour")
    val hour: Int,
    @ColumnInfo(name = "min")
    val min: Int,
    @ColumnInfo(name = "week_days")
    val weekDays: String,
    @ColumnInfo(name = "isActive")
    val isActive: Boolean
)