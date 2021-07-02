package de.coldtea.smplr.smplralarm.repository

import android.annotation.SuppressLint
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
import de.coldtea.smplr.smplralarm.repository.migrations.MIGRATION_1_2

/**
 * Created by [Yasar Naci Gündüz](https://github.com/ColdTea-Projects).
 */
@SuppressLint("RestrictedApi")
@Database(
    entities = [
        AlarmNotificationEntity::class,
        NotificationChannelEntity::class,
        NotificationEntity::class
    ],
    version = 2,
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
                        .addMigrations(MIGRATION_1_2)
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}