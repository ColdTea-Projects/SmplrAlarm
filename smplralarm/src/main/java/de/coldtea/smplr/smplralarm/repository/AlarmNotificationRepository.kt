package de.coldtea.smplr.smplralarm.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.Intent.URI_ALLOW_UNSAFE
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmAPI
import de.coldtea.smplr.smplralarm.extensions.activeDaysAsJsonString
import de.coldtea.smplr.smplralarm.extensions.activeDaysAsWeekdaysList
import de.coldtea.smplr.smplralarm.extensions.convertToNotificationItem
import de.coldtea.smplr.smplralarm.models.NotificationItem
import de.coldtea.smplr.smplralarm.models.WeekDays
import de.coldtea.smplr.smplralarm.receivers.AlarmNotification
import de.coldtea.smplr.smplralarm.receivers.extractAlarmNotificationEntity
import de.coldtea.smplr.smplralarm.receivers.extractNotificationChannelEntity
import de.coldtea.smplr.smplralarm.receivers.extractNotificationEntity
import de.coldtea.smplr.smplralarm.repository.entity.AlarmNotificationEntity
import de.coldtea.smplr.smplralarm.repository.entity.NotificationEntity
import de.coldtea.smplr.smplralarm.repository.entity.convertToNotificationChannelItem
import org.json.JSONException
import org.json.JSONObject
import java.net.URISyntaxException
import java.util.*

/**
 * Created by [Yasar Naci Gündüz](https://github.com/ColdTea-Projects).
 */
internal class AlarmNotificationRepository(
    private val context: Context
) {
    private val alarmNotificationDatabase by lazy {
        AlarmNotificationDatabase.getInstance(context)
    }

    private val sharedPreferences by lazy {
        context.getSharedPreferences(SMPLR_ALARM_INTENTS_SHARED_PREFERENCES, MODE_PRIVATE)
    }

    suspend fun insertAlarmNotification(alarmNotification: AlarmNotification) {

        saveIntent(
            SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_INTENT_PREFIX,
            alarmNotification.alarmNotificationId,
            alarmNotification.intent
        )
        saveIntent(
            SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_FULLSCREEN_INTENT_PREFIX,
            alarmNotification.alarmNotificationId,
            alarmNotification.fullScreenIntent?.putExtra(
                SmplrAlarmAPI.SMPLR_ALARM_REQUEST_ID,
                alarmNotification.alarmNotificationId
            )
        )
        saveIntent(
            SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_ALARM_RECEIVED_INTENT_PREFIX,
            alarmNotification.alarmNotificationId,
            alarmNotification.alarmReceivedIntent?.putExtra(
                SmplrAlarmAPI.SMPLR_ALARM_REQUEST_ID,
                alarmNotification.alarmNotificationId
            )
        )

        if (alarmNotification.notificationItem?.firstButtonText != null && alarmNotification.notificationItem.firstButtonIntent != null) {
            saveIntent(
                SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_FIRST_BUTTON_INTENT_PREFIX,
                alarmNotification.alarmNotificationId,
                alarmNotification.notificationItem.firstButtonIntent
            )
        }

        if (alarmNotification.notificationItem?.secondButtonText != null && alarmNotification.notificationItem.secondButtonIntent != null) {
            saveIntent(
                SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_SECOND_BUTTON_INTENT_PREFIX,
                alarmNotification.alarmNotificationId,
                alarmNotification.notificationItem.secondButtonIntent
            )
        }
        if (alarmNotification.notificationItem?.notificationDismissedIntent != null) {
            saveIntent(
                SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_DISMISS_NOTIFICATION_INTENT_PREFIX,
                alarmNotification.alarmNotificationId,
                alarmNotification.notificationItem.notificationDismissedIntent
            )
        }

        alarmNotificationDatabase.daoAlarmNotification.insert(alarmNotification.extractAlarmNotificationEntity())
        alarmNotificationDatabase.daoNotificationChannel.insert(
            alarmNotification.extractNotificationChannelEntity(
                alarmNotification.alarmNotificationId
            )
        )
        alarmNotificationDatabase.daoNotification.insert(
            alarmNotification.extractNotificationEntity(
                alarmNotification.alarmNotificationId
            )
        )

    }

    suspend fun updateAlarmNotification(
        alarmNotificationId: Int,
        hour: Int,
        min: Int,
        weekDays: List<WeekDays>?,
        isActive: Boolean,
        infoPairs: String
    ) {
        val updatedWeekDays = weekDays ?: listOf()

        val newAlarmNotificationEntity = AlarmNotificationEntity(
            alarmNotificationId,
            hour,
            min,
            updatedWeekDays.activeDaysAsJsonString(),
            isActive,
            infoPairs
        )

        alarmNotificationDatabase.daoAlarmNotification.update(newAlarmNotificationEntity)

    }

    @Throws(IllegalArgumentException::class)
    suspend fun getAlarmNotification(intentId: Int): AlarmNotification {
        val alarmNotification =
            alarmNotificationDatabase.daoAlarmNotification.getAlarmNotification(intentId)
                .firstOrNull()
                ?: throw IllegalArgumentException()
        val fullScreenIntent = retrieveIntent(
            SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_FULLSCREEN_INTENT_PREFIX,
            intentId
        )
        val intent = retrieveIntent(
            SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_INTENT_PREFIX,
            intentId
        )
        val alarmReceivedIntent = retrieveIntent(
            SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_ALARM_RECEIVED_INTENT_PREFIX,
            intentId
        )

        val notificationItemWithButtons =
            alarmNotification.notificationEntity.convertToNotificationItem().apply {
                if (firstButtonText != null) {
                    firstButtonIntent = retrieveIntent(
                        SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_FIRST_BUTTON_INTENT_PREFIX,
                        alarmNotification.alarmNotificationEntity.alarmNotificationId
                    )
                }
                if (secondButtonText != null) {
                    secondButtonIntent = retrieveIntent(
                        SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_SECOND_BUTTON_INTENT_PREFIX,
                        alarmNotification.alarmNotificationEntity.alarmNotificationId
                    )
                }

                notificationDismissedIntent = retrieveIntent(
                    SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_DISMISS_NOTIFICATION_INTENT_PREFIX,
                    alarmNotification.alarmNotificationEntity.alarmNotificationId
                )
            }



        return AlarmNotification(
            alarmNotificationId = intentId,
            hour = alarmNotification.alarmNotificationEntity.hour,
            min = alarmNotification.alarmNotificationEntity.min,
            weekDays = alarmNotification.alarmNotificationEntity.activeDaysAsWeekdaysList()
                ?: listOf(),
            notificationChannelItem = alarmNotification.notificationChannelEntity.convertToNotificationChannelItem(),
            notificationItem = notificationItemWithButtons,
            intent = intent,
            fullScreenIntent = fullScreenIntent,
            alarmReceivedIntent = alarmReceivedIntent,
            isActive = alarmNotification.alarmNotificationEntity.isActive,
            infoPairs = alarmNotification.alarmNotificationEntity.infoPairs
        )
    }

    suspend fun getAllAlarmNotifications(): List<AlarmNotification> =
        alarmNotificationDatabase.daoAlarmNotification.getAllAlarmNotification()
            .map { alarmNotification ->

                val fullScreenIntent = retrieveIntent(
                    SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_FULLSCREEN_INTENT_PREFIX,
                    alarmNotification.alarmNotificationEntity.alarmNotificationId
                )
                val intent = retrieveIntent(
                    SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_INTENT_PREFIX,
                    alarmNotification.alarmNotificationEntity.alarmNotificationId
                )
                val alarmReceivedIntent = retrieveIntent(
                    SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_ALARM_RECEIVED_INTENT_PREFIX,
                    alarmNotification.alarmNotificationEntity.alarmNotificationId
                )

                val notificationItemWithButtons =
                    alarmNotification.notificationEntity.convertToNotificationItem()
                        .apply {
                            if (firstButtonText != null) {
                                firstButtonIntent = retrieveIntent(
                                    SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_FIRST_BUTTON_INTENT_PREFIX,
                                    alarmNotification.alarmNotificationEntity.alarmNotificationId
                                )
                            }
                            if (secondButtonText != null) {
                                secondButtonIntent = retrieveIntent(
                                    SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_SECOND_BUTTON_INTENT_PREFIX,
                                    alarmNotification.alarmNotificationEntity.alarmNotificationId
                                )
                            }

                            notificationDismissedIntent = retrieveIntent(
                                SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_DISMISS_NOTIFICATION_INTENT_PREFIX,
                                alarmNotification.alarmNotificationEntity.alarmNotificationId
                            )
                        }

                AlarmNotification(
                    alarmNotificationId = alarmNotification.alarmNotificationEntity.alarmNotificationId,
                    hour = alarmNotification.alarmNotificationEntity.hour,
                    min = alarmNotification.alarmNotificationEntity.min,
                    weekDays = alarmNotification.alarmNotificationEntity.activeDaysAsWeekdaysList()
                        ?: listOf(),
                    notificationChannelItem = alarmNotification.notificationChannelEntity.convertToNotificationChannelItem(),
                    notificationItem = notificationItemWithButtons,
                    intent = intent,
                    fullScreenIntent = fullScreenIntent,
                    alarmReceivedIntent = alarmReceivedIntent,
                    isActive = alarmNotification.alarmNotificationEntity.isActive,
                    infoPairs = alarmNotification.alarmNotificationEntity.infoPairs
                )
            }

    suspend fun deleteAlarmNotification(intentId: Int) {

        val alarmNotification =
            alarmNotificationDatabase.daoAlarmNotification.getAlarmNotification(intentId).first()

        alarmNotificationDatabase.daoNotificationChannel.delete(alarmNotification.notificationChannelEntity)
        alarmNotificationDatabase.daoNotification.delete(alarmNotification.notificationEntity)
        alarmNotificationDatabase.daoAlarmNotification.delete(alarmNotification.alarmNotificationEntity)

    }

    /**
     * This function attempts to deactivate the alarm from database. If there are weekdays assigned,
     * it does not delete. In either case, it let's the caller know if the record deactivated.
     *
     * @param intentId primary key of the alarm
     */
    suspend fun deactivateSingleAlarmNotification(intentId: Int) {
        val alarmNotification =
            alarmNotificationDatabase.daoAlarmNotification.getAlarmNotification(intentId).first()

        updateAlarmNotification(
            alarmNotification.alarmNotificationEntity.alarmNotificationId,
            alarmNotification.alarmNotificationEntity.hour,
            alarmNotification.alarmNotificationEntity.min,
            listOf(),
            false,
            alarmNotification.alarmNotificationEntity.infoPairs
        )
    }

    suspend fun deleteAlarmsBeforeNow() {

        val calendar = Calendar.getInstance()

        alarmNotificationDatabase.daoNotificationChannel.deleteNotificationBefore(calendar.timeInMillis.toInt())
        alarmNotificationDatabase.daoNotification.deleteNotificationBefore(calendar.timeInMillis.toInt())
        alarmNotificationDatabase.daoAlarmNotification.deleteNotificationBefore(calendar.timeInMillis.toInt())

    }

    suspend fun updateNotification(intentId: Int, notificationItem: NotificationItem) {
        val currentNotification =
            alarmNotificationDatabase.daoNotification.getNotificationById(intentId)
        val updatedNotificationEntity = NotificationEntity(
            currentNotification.notificationId,
            currentNotification.fkAlarmNotificationId,
            notificationItem.smallIcon ?: currentNotification.smallIcon,
            notificationItem.title ?: currentNotification.title,
            notificationItem.message ?: currentNotification.message,
            notificationItem.bigText ?: currentNotification.bigText,
            notificationItem.autoCancel ?: currentNotification.autoCancel,
            notificationItem.firstButtonText ?: currentNotification.firstButton,
            notificationItem.secondButtonText ?: currentNotification.secondButton
        )

        alarmNotificationDatabase.daoNotification.update(updatedNotificationEntity)

        if (notificationItem.firstButtonText != null && notificationItem.firstButtonIntent != null) {
            saveIntent(
                SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_FIRST_BUTTON_INTENT_PREFIX,
                intentId,
                notificationItem.firstButtonIntent
            )
        }

        if (notificationItem.secondButtonText != null && notificationItem.secondButtonIntent != null) {
            saveIntent(
                SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_SECOND_BUTTON_INTENT_PREFIX,
                intentId,
                notificationItem.secondButtonIntent
            )
        }
        if (notificationItem.notificationDismissedIntent != null) {
            saveIntent(
                SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_DISMISS_NOTIFICATION_INTENT_PREFIX,
                intentId,
                notificationItem.notificationDismissedIntent
            )
        }
    }

    private fun saveIntent(prefix: String, requestId: Int, intent: Intent?) {
        if (intent == null) return

        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putString(prefix.plus(requestId), intent.toUri(URI_ALLOW_UNSAFE))

        val extrasKeySet = intent.extras?.keySet()

        sharedPreferencesEditor.putStringSet(
            prefix.plus(
                SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_KEYSET_PREFIX
            ).plus(requestId), extrasKeySet
        )

        val jsonObject = JSONObject()
        extrasKeySet?.map {
            try {
                jsonObject.put(it, JSONObject.wrap(intent.extras?.get(it)))
            } catch (ex: JSONException) {

            }
        }

        sharedPreferencesEditor.putString(
            prefix.plus(
                SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_BUNDLE_PREFIX
            ).plus(requestId), jsonObject.toString()
        )
        sharedPreferencesEditor.apply()
    }

    private fun retrieveIntent(prefix: String, requestId: Int): Intent? {
        try {
            val uri = sharedPreferences.getString(prefix.plus(requestId), null) ?: return null

            val intent = Intent.parseUri(
                uri,
                URI_ALLOW_UNSAFE
            )


            val extrasKeySet = sharedPreferences.getStringSet(
                prefix.plus(SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_KEYSET_PREFIX).plus(requestId),
                null
            )
            val jsonObject = JSONObject(
                sharedPreferences.getString(
                    prefix.plus(SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_BUNDLE_PREFIX)
                        .plus(requestId), ""
                ) ?: ""
            )

            extrasKeySet?.map {
                when (jsonObject.get(it)) {
                    is String -> intent.putExtra(it, jsonObject.getString(it))
                    is Int -> intent.putExtra(it, jsonObject.getInt(it))
                    is Long -> intent.putExtra(it, jsonObject.getLong(it))
                    is Double -> intent.putExtra(it, jsonObject.getDouble(it))
                    else -> {}
                }
            }

            return intent
        } catch (ex: URISyntaxException) {
            return null
        }

    }


    companion object {
        private var SMPLR_ALARM_INTENTS_SHARED_PREFERENCES =
            "smplr_alarm_intents_shared_preferences"
        private var SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_INTENT_PREFIX = "INTENT_"
        private var SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_FULLSCREEN_INTENT_PREFIX =
            "FULLSCREEN_INTENT_"
        private var SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_ALARM_RECEIVED_INTENT_PREFIX =
            "ALARM_RECEIVED_INTENT_"

        private var SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_BUNDLE_PREFIX = "BUNDLE_"
        private var SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_KEYSET_PREFIX = "KEYSET_"


        private var SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_FIRST_BUTTON_INTENT_PREFIX =
            "FIRST_BUTTON_INTENT_"
        private var SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_SECOND_BUTTON_INTENT_PREFIX =
            "SECOND_BUTTON_INTENT_"
        private var SMPLR_ALARM_INTENTS_SHARED_PREFERENCES_DISMISS_NOTIFICATION_INTENT_PREFIX =
            "DISMISS_NOTIFICATION_INTENT_"
    }
}