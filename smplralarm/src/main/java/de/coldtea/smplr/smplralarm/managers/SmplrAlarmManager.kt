package de.coldtea.smplr.smplralarm.managers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import de.coldtea.smplr.smplralarm.extensions.getTimeExactForAlarmInMilliseconds
import de.coldtea.smplr.smplralarm.models.NotificationChannelItem
import de.coldtea.smplr.smplralarm.models.NotificationItem
import de.coldtea.smplr.smplralarm.receivers.AlarmNotification
import de.coldtea.smplr.smplralarm.receivers.AlarmReceiver
import de.coldtea.smplr.smplralarm.receivers.SmplrAlarmReceiverObjects.Companion.SMPLR_ALARM_RECEIVER_INTENT_ID
import de.coldtea.smplr.smplralarm.receivers.SmplrAlarmReceiverObjects.Companion.alarmNotification
import de.coldtea.smplr.smplralarm.models.WeekDays
import de.coldtea.smplr.smplralarm.repository.AlarmNotificationRepository
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.Calendar
import kotlin.math.absoluteValue

typealias AlarmRingEvent = (Int) -> Unit

class SmplrAlarmManager(val context: Context) {

    //region properties

    var hour = -1
    var min = -1
    var requestCode = -1
    var intent: Intent? = null
    var fullScreenIntent: Intent? = null

    var notificationChannel: NotificationChannelItem? = null
    var notification: NotificationItem? = null

    var alarmRingEvent: AlarmRingEvent? = null

    var weekdays: List<WeekDays> = listOf()

    //endregion

    // region computed properties

    private val alarmManager: AlarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

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

    fun fullScreenIntent(fullScreenIntent: () -> Intent) {
        this.fullScreenIntent = fullScreenIntent()
    }

    fun notificationChannel(notificationChannel: () -> NotificationChannelItem) {
        this.notificationChannel = notificationChannel()
    }

    fun notification(notification: () -> NotificationItem) {
        this.notification = notification()
    }

    fun onAlarmRings(alarmRingEvent: AlarmRingEvent) {
        this.alarmRingEvent = alarmRingEvent
    }

    fun weekdays(lambda: WeekDaysManager.() -> Unit) {
        weekdays = WeekDaysManager().apply(lambda).getWeekDays()
    }

    // endregion

    // region functionality

    fun setAlarm(): Int {

        val calendar = Calendar.getInstance()
        requestCode = getUniqueIdBasedNow()
        Timber.v("SmplrAlarm.AlarmManager.setAlarm: $requestCode -- $hour:$min")

        val pendingIntent = createPendingIntent()
        val notifiactionBuilderItem = createAlarmNotification()

        CoroutineScope(Dispatchers.IO).launch {
            saveAlarmNotificationToDatabase(notifiactionBuilderItem)
        }

        val exactAlarmTime = calendar.getTimeExactForAlarmInMilliseconds(
            hour,
            min,
            weekdays
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            exactAlarmTime,
            pendingIntent
        )

        return requestCode
    }

    fun cancelAlarm() {
        Timber.v("SmplrAlarm.AlarmManager.cancelAlarm: $requestCode -- $hour:$min")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = getPendingIntent()

        alarmManager.cancel(pendingIntent)

        CoroutineScope(Dispatchers.IO).launch {
            deleteAlarmNotificationFromDatabase()
        }
    }

    private fun createPendingIntent() = PendingIntent.getBroadcast(
        context,
        requestCode,
        AlarmReceiver.build(context).putExtra(SMPLR_ALARM_RECEIVER_INTENT_ID, requestCode),
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    private fun getPendingIntent() = PendingIntent.getBroadcast(
        context,
        requestCode,
        Intent(context, AlarmReceiver::class.java),
        PendingIntent.FLAG_NO_CREATE
    )

    private fun createAlarmNotification() = AlarmNotification(
        requestCode,
        hour,
        min,
        weekdays,
        notificationChannel
            ?: ChannelManager().build(),
        notification
            ?: AlarmNotificationManager().build(),
        intent,
        fullScreenIntent,
        alarmRingEvent
    )

    private suspend fun saveAlarmNotificationToDatabase(notificationBuilderItem: AlarmNotification) {
            try {
                alarmNotificationRepository.insertAlarmNotification(notificationBuilderItem)
                alarmNotification.add(notificationBuilderItem)
            } catch (exception: Exception) {
                Timber.e("SmplrAlarm.AlarmManager.saveAlarmNotificationToDatabase: Alarm Notification could not be saved to the database --> $exception")
            }
        }

    private suspend fun deleteAlarmNotificationFromDatabase() {
        try {
            alarmNotificationRepository.deleteAlarmNotification(requestCode)
        } catch (exception: Exception) {
            Timber.e("SmplrAlarm.AlarmManager.saveAlarmNotificationToDatabase: Alarm Notification with id $requestCode could not be removed from the database --> $exception")
        }
    }

    private fun getUniqueIdBasedNow() = System.currentTimeMillis().toInt().absoluteValue

    // endregion
}
