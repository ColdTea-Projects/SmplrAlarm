package de.coldtea.smplr.smplralarm.repository.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import de.coldtea.smplr.smplralarm.repository.entity.AlarmNotificationEntity
import de.coldtea.smplr.smplralarm.repository.relations.AlarmNotifications

@Dao
abstract class DaoAlarmNotification : DaoBase<AlarmNotificationEntity> {

    @Transaction
    @Query("SELECT * From alarm_notification_table WHERE alarm_notification_id = :alarmNotificaionId")
    abstract suspend fun getAlarmNotification(alarmNotificaionId: Int) : List<AlarmNotifications>

}