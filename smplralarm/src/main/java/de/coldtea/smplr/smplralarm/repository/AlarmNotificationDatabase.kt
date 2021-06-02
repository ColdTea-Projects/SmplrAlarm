package de.coldtea.smplr.smplralarm.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.coldtea.smplr.smplralarm.repository.dao.DaoAlarmNotification
import de.coldtea.smplr.smplralarm.repository.dao.DaoNotification
import de.coldtea.smplr.smplralarm.repository.dao.DaoNotificationChannel
import de.coldtea.smplr.smplralarm.repository.entity.AlarmNotificationEntity
import de.coldtea.smplr.smplralarm.repository.entity.NotificationChannelEntity
import de.coldtea.smplr.smplralarm.repository.entity.NotificationEntity

/**
 * Created by [Yasar Naci Gündüz](https://github.com/ColdTea-Projects).
 */
@Database(
    entities = [
        AlarmNotificationEntity::class,
        NotificationChannelEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
internal abstract class AlarmNotificationDatabase : RoomDatabase() {

    abstract val daoAlarmNotification: DaoAlarmNotification
    abstract val daoNotificationChannel: DaoNotificationChannel
    abstract val daoNotification: DaoNotification

    companion object {
        @Volatile
        private var INSTANCE: AlarmNotificationDatabase? = null
        internal fun getInstance(context: Context): AlarmNotificationDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AlarmNotificationDatabase::class.java,
                        "db_smplr_alarm"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}