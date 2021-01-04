package de.coldtea.smplr.smplralarm.repository.dao

import androidx.room.Dao
import androidx.room.Query
import de.coldtea.smplr.smplralarm.repository.entity.NotificationChannelEntity

@Dao
abstract class DaoNotificationChannel : DaoBase<NotificationChannelEntity> {

    @Query("SELECT * From notification_channel_table")
    abstract fun getNotificationChannel() : List<NotificationChannelEntity>

}