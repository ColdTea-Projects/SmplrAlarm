package de.coldtea.smplr.smplralarm.apis

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import de.coldtea.smplr.smplralarm.models.NotificationChannelItem
import de.coldtea.smplr.smplralarm.models.NotificationItem
import de.coldtea.smplr.smplralarm.models.WeekDays
import de.coldtea.smplr.smplralarm.receivers.AlarmNotification
import de.coldtea.smplr.smplralarm.receivers.AlarmReceiver
import de.coldtea.smplr.smplralarm.receivers.SmplrAlarmReceiverObjects.Companion.SMPLR_ALARM_RECEIVER_INTENT_ID
import de.coldtea.smplr.smplralarm.receivers.SmplrAlarmReceiverObjects.Companion.alarmNotification
import de.coldtea.smplr.smplralarm.repository.AlarmNotificationRepository
import de.coldtea.smplr.smplralarm.services.AlarmService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.absoluteValue

typealias AlarmRingEvent = (Int) -> Unit

class SmplrAlarmAPI(val context: Context) {

    //region properties

    private var hour = -1
    private var min = -1
    private var weekdays: List<WeekDays> = listOf()
    private var isActive: Boolean = true

    private var requestCode = -1
    private var intent: Intent? = null
    private var receiverIntent: Intent? = null

    private var notificationChannel: NotificationChannelItem? = null
    private var notification: NotificationItem? = null

    private var requestAPI: SmplrAlarmListRequestAPI? = null

    private val alarmService by lazy { AlarmService(context) }

    private val isAlarmValid: Boolean
        get() = hour > -1
                && hour < 24
                && min > -1
                && min < 60

    //endregion

    // region computed properties

    private val alarmNotificationRepository: AlarmNotificationRepository by lazy {
        AlarmNotificationRepository(context)
    }

    // endregion

    // region DSL setters
    fun hour(hour: () -> Int) {
        this.hour = hour()
    }

    fun min(min: () -> Int) {
        this.min = min()
    }

    fun requestCode(requestCode: () -> Int) {
        this.requestCode = requestCode()
    }

    fun intent(intent: () -> Intent) {
        this.intent = intent()
    }

    fun receiverIntent(receiverIntent: () -> Intent) {
        this.receiverIntent = receiverIntent()
    }

    fun notificationChannel(notificationChannel: () -> NotificationChannelItem) {
        this.notificationChannel = notificationChannel()
    }

    fun notification(notification: () -> NotificationItem) {
        this.notification = notification()
    }

    fun weekdays(lambda: WeekDaysAPI.() -> Unit) {
        weekdays = WeekDaysAPI().apply(lambda).getWeekDays()
    }

    fun isActive(isActive: () -> Boolean) {
        this.isActive = isActive()
    }

    fun requestAPI(requestAPI: () -> SmplrAlarmListRequestAPI) {
        this.requestAPI = requestAPI()
    }

    // endregion

    // region functionality

    internal fun setAlarm(): Int {
        if (isAlarmValid.not()) {
            Timber.w("SmplrAlarmApp.SmplrAlarmManager.updateRepeatingAlarm: Your time setup is not valid, please pick a valid time! ")
            return -1
        }

        requestCode = getTimeBaseUniqueId()
        Timber.v("SmplrAlarm.AlarmManager.setAlarm: $requestCode -- $hour:$min")

        val notifictionBuilderItem = createAlarmNotification()

        CoroutineScope(Dispatchers.IO).launch {
            saveAlarmNotificationToDatabase(notifictionBuilderItem)
            requestAPI?.requestAlarmList()
        }

        alarmService.setAlarm(requestCode, hour, min, weekdays)

        return requestCode
    }

    internal fun renewMissingAlarms() = CoroutineScope(Dispatchers.IO).launch {
        val notificationRepository = AlarmNotificationRepository(context)
        try {
            val alarmNotifications = notificationRepository.getAllAlarmNotifications()

            alarmNotifications
                .filter { it.isActive && !alarmService.alarmExist(it.alarmNotificationId) }
                .map {
                    alarmService.renewAlarm(it)
                }
        } catch (ex: Exception) {
            Timber.e("SmplrAlarmApp.SmplrAlarmManager.updateRepeatingAlarm: $ex ")
        }
    }

    internal fun updateAlarm() {
        if (requestCode == -1) return
        if (isAlarmValid.not()) {
            Timber.w("SmplrAlarmApp.SmplrAlarmManager.updateRepeatingAlarm: Your time setup is not valid, please pick a valid time! ")
            return
        }

        alarmService.cancelAlarm(requestCode)
        val updatedActivation = isActive

        CoroutineScope(Dispatchers.IO).launch {
            val notificationRepository = AlarmNotificationRepository(context)
            try {
                val alarmNotification = notificationRepository.getAlarmNotification(requestCode)

                val updatedHour = if (hour == -1) alarmNotification.hour else hour
                val updatedMinute = if (min == -1) alarmNotification.min else min

                updateAlarmNotification(
                    requestCode,
                    updatedHour,
                    updatedMinute,
                    weekdays,
                    updatedActivation
                )
                if (updatedActivation) alarmService.setAlarm(
                    requestCode,
                    updatedHour,
                    updatedMinute,
                    weekdays
                )

                requestAPI?.requestAlarmList()
            } catch (ex: IllegalArgumentException) {
                Timber.e("SmplrAlarmApp.SmplrAlarmManager.updateRepeatingAlarm: The alarm intended to be removed does not exist! ")
            } catch (ex: Exception) {
                Timber.e("SmplrAlarmApp.SmplrAlarmManager.updateRepeatingAlarm: $ex ")
            }
        }
    }

    internal fun removeAlarm() {
        alarmService.cancelAlarm(requestCode)

        CoroutineScope(Dispatchers.IO).launch {
            deleteAlarmNotificationFromDatabase()
            requestAPI?.requestAlarmList()
        }
    }

    private fun createAlarmNotification() = AlarmNotification(
        requestCode,
        hour,
        min,
        weekdays,
        notificationChannel
            ?: ChannelManagerAPI().build(),
        notification
            ?: AlarmNotificationAPI().build(),
        intent,
        receiverIntent,
        true
    )

    private suspend fun saveAlarmNotificationToDatabase(notificationBuilderItem: AlarmNotification) {
        try {
            alarmNotificationRepository.insertAlarmNotification(notificationBuilderItem)
            alarmNotification.add(notificationBuilderItem)
        } catch (exception: Exception) {
            Timber.e("SmplrAlarm.AlarmManager.saveAlarmNotificationToDatabase: Alarm Notification could not be saved to the database --> $exception")
        }
    }

    private suspend fun updateAlarmNotification(
        alarmNotificationId: Int,
        hour: Int,
        min: Int,
        weekDays: List<WeekDays>?,
        isActive: Boolean
    ) {

        try {
            alarmNotificationRepository.updateAlarmNotification(
                alarmNotificationId,
                hour,
                min,
                weekDays,
                isActive
            )
        } catch (exception: Exception) {
            Timber.e("SmplrAlarm.AlarmManager.saveAlarmNotificationToDatabase: Alarm Notification could not be updated to the database --> $exception")
        }
    }

    private suspend fun deleteAlarmNotificationFromDatabase() {
        try {
            alarmNotificationRepository.deleteAlarmNotification(requestCode)
        } catch (exception: Exception) {
            Timber.e("SmplrAlarm.AlarmManager.saveAlarmNotificationToDatabase: Alarm Notification with id $requestCode could not be removed from the database --> $exception")
        }
    }

    private fun getTimeBaseUniqueId() = System.currentTimeMillis().toInt().absoluteValue
    // endregion

    companion object {

        const val SMPLR_ALARM_NOTIFICATION_ID = "smplr_alarm_notification_id"

        fun getAlarmIntent(requestCode: Int, context: Context) = PendingIntent.getBroadcast(
            context,
            requestCode,
            AlarmReceiver.build(context).putExtra(SMPLR_ALARM_RECEIVER_INTENT_ID, requestCode),
            PendingIntent.FLAG_NO_CREATE
        )

    }
}
