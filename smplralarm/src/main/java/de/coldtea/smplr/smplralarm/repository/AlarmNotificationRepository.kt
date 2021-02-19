package de.coldtea.smplr.smplralarm.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.Intent.URI_ALLOW_UNSAFE
import de.coldtea.smplr.smplralarm.extensions.activeDaysAsWeekdaysList
import de.coldtea.smplr.smplralarm.receivers.AlarmNotification
import de.coldtea.smplr.smplralarm.receivers.extractAlarmNotificationEntity
import de.coldtea.smplr.smplralarm.receivers.extractNotificationChannelEntity
import de.coldtea.smplr.smplralarm.receivers.extractNotificationEntity
import de.coldtea.smplr.smplralarm.repository.entity.convertToNotificationChannelItem
import de.coldtea.smplr.smplralarm.repository.entity.convertToNotificationItem
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.net.URISyntaxException
import java.util.*

class AlarmNotificationRepository(
    private val context: Context
) {
    private val alarmNotificationDatabase by lazy {
        AlarmNotificationDatabase.getInstance(context)
    }

    private val sharedPreferences by lazy {
        context.getSharedPreferences(SMPLR_ALARM_INTENTS_SHARED_PREFERENCES, MODE_PRIVATE)
    }

    suspend fun insertAlarmNotification(alarmNotification: AlarmNotification){

        saveIntent(SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_INTENT_PREFIX, alarmNotification.alarmNotificationId, alarmNotification.intent)
        saveIntent(SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_FULLSCREEN_INTENT_PREFIX, alarmNotification.alarmNotificationId, alarmNotification.fullScreenIntent)

        alarmNotificationDatabase.daoAlarmNotification.insert(alarmNotification.extractAlarmNotificationEntity())
        alarmNotificationDatabase.daoNotificationChannel.insert(alarmNotification.extractNotificationChannelEntity(alarmNotification.alarmNotificationId))
        alarmNotificationDatabase.daoNotification.insert(alarmNotification.extractNotificationEntity(alarmNotification.alarmNotificationId))

    }

    suspend fun getAlarmNotification(intentId: Int): AlarmNotification{
        val alarmNotification = alarmNotificationDatabase.daoAlarmNotification.getAlarmNotification(intentId).first()

        return AlarmNotification(
            intentId,
            alarmNotification.alarmNotificationEntity.hour,
            alarmNotification.alarmNotificationEntity.min,
            alarmNotification.alarmNotificationEntity.weekDays.activeDaysAsWeekdaysList()?: listOf(),
            alarmNotification.notificationChannelEntity.convertToNotificationChannelItem(),
            alarmNotification.notificationEntity.convertToNotificationItem(),
            null,
            retrieveIntent(SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_FULLSCREEN_INTENT_PREFIX, intentId)
        ) {}//TODO: implement intents
    }

    suspend fun getAllAlarmNotifications(): List<AlarmNotification> =
        alarmNotificationDatabase.daoAlarmNotification.getAllAlarmNotification().map { alarmNotification ->
            AlarmNotification(
                alarmNotification.alarmNotificationEntity.alarmNotificationId,
                alarmNotification.alarmNotificationEntity.hour,
                alarmNotification.alarmNotificationEntity.min,
                alarmNotification.alarmNotificationEntity.weekDays.activeDaysAsWeekdaysList()?: listOf(),
                alarmNotification.notificationChannelEntity.convertToNotificationChannelItem(),
                alarmNotification.notificationEntity.convertToNotificationItem(),
                null,
                retrieveIntent(SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_FULLSCREEN_INTENT_PREFIX, alarmNotification.alarmNotificationEntity.alarmNotificationId)
            ) {}
        }

    suspend fun deleteAlarmNotification(intentId: Int) {

        val alarmNotification = alarmNotificationDatabase.daoAlarmNotification.getAlarmNotification(intentId).first()

        alarmNotificationDatabase.daoNotificationChannel.delete(alarmNotification.notificationChannelEntity)
        alarmNotificationDatabase.daoNotification.delete(alarmNotification.notificationEntity)
        alarmNotificationDatabase.daoAlarmNotification.delete(alarmNotification.alarmNotificationEntity)

    }

    suspend fun deleteAlarmsBeforeNow(){

        val calendar = Calendar.getInstance()

        alarmNotificationDatabase.daoNotificationChannel.deleteNotificationBefore(calendar.timeInMillis.toInt())
        alarmNotificationDatabase.daoNotification.deleteNotificationBefore(calendar.timeInMillis.toInt())
        alarmNotificationDatabase.daoAlarmNotification.deleteNotificationBefore(calendar.timeInMillis.toInt())

    }

    private fun saveIntent(prefix: String, requestId: Int, intent: Intent?) {
        if (intent == null) return

        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putString(prefix.plus(requestId), intent.toUri(URI_ALLOW_UNSAFE))

        val extrasKeySet = intent.extras?.keySet()

        sharedPreferencesEditor.putStringSet(prefix.plus(SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_KEYSET_PREFIX).plus(requestId), extrasKeySet)

        val jsonObject = JSONObject()
        extrasKeySet?.map {
            try {
                jsonObject.put(it, JSONObject.wrap(intent.extras?.get(it)))
            }catch (ex: JSONException){

            }
        }

        sharedPreferencesEditor.putString(prefix.plus(SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_BUNDLE_PREFIX).plus(requestId), jsonObject.toString())
        sharedPreferencesEditor.apply()
    }

    private fun retrieveIntent(prefix: String, requestId: Int): Intent? =
        try {

            val intent = Intent.parseUri(sharedPreferences.getString(prefix.plus(requestId), null), URI_ALLOW_UNSAFE)
            val extrasKeySet = sharedPreferences.getStringSet(prefix.plus(SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_KEYSET_PREFIX).plus(requestId), null)
            val jsonObject = JSONObject(sharedPreferences.getString(prefix.plus(SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_BUNDLE_PREFIX).plus(requestId),"")?:"")

            extrasKeySet?.map {
                intent.putExtra(it, jsonObject.get(it).toString())
            }

            intent

        }catch (ex: URISyntaxException){
            null
        }




    companion object{
        private var SMPLR_ALARM_INTENTS_SHARED_PREFERENCES = "smplr_alarm_intents_shared_preferences"
        private var SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_INTENT_PREFIX = "INTENT_"
        private var SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_FULLSCREEN_INTENT_PREFIX = "FULLSCREEN_INTENT_"
        private var SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_BUNDLE_PREFIX = "BUNDLE_"
        private var SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_KEYSET_PREFIX = "KEYSET_"
    }
}