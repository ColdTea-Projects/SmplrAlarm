package de.coldtea.smplr.smplralarm.repository.dao

import androidx.room.Dao
import androidx.room.Query
import de.coldtea.smplr.smplralarm.repository.entity.NotificationEntity

@Dao
abstract class DaoNotification : DaoBase<NotificationEntity> {

    @Query("SELECT * From notification_table")
    abstract fun getNotification() : List<NotificationEntity>

}