package de.coldtea.smplr.smplralarm.repository.dao

import androidx.room.Dao
import androidx.room.Query
import de.coldtea.smplr.smplralarm.repository.entity.NotificationEntity

/**
 * Created by [Yasar Naci Gündüz](https://github.com/ColdTea-Projects?tab=following).
 */
@Dao
internal abstract class DaoNotification : DaoBase<NotificationEntity> {

    @Query("SELECT * From notification_table")
    abstract suspend fun getNotification() : List<NotificationEntity>

    @Query("DELETE From notification_table WHERE fk_alarm_notification_id < :timestamp" )
    abstract suspend fun deleteNotificationBefore(timestamp: Int)

}