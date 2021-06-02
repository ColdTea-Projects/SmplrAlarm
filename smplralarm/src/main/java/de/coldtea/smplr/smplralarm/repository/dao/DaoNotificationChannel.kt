package de.coldtea.smplr.smplralarm.repository.dao

import androidx.room.Dao
import androidx.room.Query
import de.coldtea.smplr.smplralarm.repository.entity.NotificationChannelEntity

/**
 * Created by [Yasar Naci Gündüz](https://github.com/ColdTea-Projects).
 */
@Dao
internal abstract class DaoNotificationChannel : DaoBase<NotificationChannelEntity> {

    @Query("SELECT * From notification_channel_table")
    abstract suspend fun getNotificationChannel() : List<NotificationChannelEntity>

    @Query("DELETE From notification_channel_table WHERE fk_alarm_notification_id < :timestamp" )
    abstract suspend fun deleteNotificationBefore(timestamp: Int)

}