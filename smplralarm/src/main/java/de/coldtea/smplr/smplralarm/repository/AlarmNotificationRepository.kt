package de.coldtea.smplr.smplralarm.repository

import android.content.Context
import android.content.Intent
import de.coldtea.smplr.smplralarm.models.NotificationChannelItem
import de.coldtea.smplr.smplralarm.receivers.AlarmNotification
import de.coldtea.smplr.smplralarm.receivers.extractAlarmNotificationEntity
import de.coldtea.smplr.smplralarm.receivers.extractNotificationChannelEntity
import de.coldtea.smplr.smplralarm.receivers.extractNotificationEntity
import de.coldtea.smplr.smplralarm.repository.entity.convertToNotificationChannelItem
import de.coldtea.smplr.smplralarm.repository.entity.convertToNotificationItem

class AlarmNotificationRepository(
    private val context: Context
) {
    private val alarmNotificationDatabase by lazy {
        AlarmNotificationDatabase.getInstance(context)
    }

    suspend fun insertAlarmNotification(alarmNotification: AlarmNotification){

        val intentId = saveIntent(alarmNotification.intent)
        val fullScreenIntentId = saveIntent(alarmNotification.fullScreenIntent)

        alarmNotificationDatabase.daoAlarmNotification.insert(alarmNotification.extractAlarmNotificationEntity(intentId, fullScreenIntentId))
        alarmNotificationDatabase.daoNotificationChannel.insert(alarmNotification.extractNotificationChannelEntity(alarmNotification.alarmNotificationId))
        alarmNotificationDatabase.daoNotification.insert(alarmNotification.extractNotificationEntity(alarmNotification.alarmNotificationId))

    }

    suspend fun getAlarmNotification(intentId: Int): AlarmNotification{
        val alarmNotification = alarmNotificationDatabase.daoAlarmNotification.getAlarmNotification(intentId).first()

        return AlarmNotification(
            intentId,
            alarmNotification.notificationChannelEntity.convertToNotificationChannelItem(),
            alarmNotification.notificationEntity.convertToNotificationItem(),
            null,
            null
        ) {}//TODO: implement intents
    }

    suspend fun deleteAlarmNotification(intentId: Int) {

        val alarmNotification = alarmNotificationDatabase.daoAlarmNotification.getAlarmNotification(intentId).first()

        alarmNotificationDatabase.daoNotificationChannel.delete(alarmNotification.notificationChannelEntity)
        alarmNotificationDatabase.daoNotification.delete(alarmNotification.notificationEntity)
        alarmNotificationDatabase.daoAlarmNotification.delete(alarmNotification.alarmNotificationEntity)

    }

    fun saveIntent(intent: Intent?):Int = -1
}