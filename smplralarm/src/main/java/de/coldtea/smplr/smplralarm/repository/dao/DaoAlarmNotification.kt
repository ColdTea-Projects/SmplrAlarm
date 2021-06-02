package de.coldtea.smplr.smplralarm.repository.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import de.coldtea.smplr.smplralarm.repository.entity.AlarmNotificationEntity
import de.coldtea.smplr.smplralarm.repository.relations.AlarmNotifications

/**
 * Created by [Yasar Naci Gündüz](https://github.com/ColdTea-Projects).
 */
@Dao
internal abstract class DaoAlarmNotification : DaoBase<AlarmNotificationEntity> {

    @Transaction
    @Query("SELECT * From alarm_notification_table WHERE alarm_notification_id = :alarmNotificaionId")
    abstract suspend fun getAlarmNotification(alarmNotificaionId: Int) : List<AlarmNotifications>

    @Transaction
    @Query("SELECT * From alarm_notification_table")
    abstract suspend fun getAllAlarmNotification() : List<AlarmNotifications>

    @Query("DELETE From alarm_notification_table WHERE alarm_notification_id < :timestamp" )
    abstract suspend fun deleteNotificationBefore(timestamp: Int)

}