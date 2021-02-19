package de.coldtea.smplr.smplralarm.managers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import de.coldtea.smplr.smplralarm.extensions.activeDaysAsJsonString
import de.coldtea.smplr.smplralarm.extensions.activeDaysAsWeekdaysList
import de.coldtea.smplr.smplralarm.extensions.getClosestDay
import de.coldtea.smplr.smplralarm.extensions.getTimeExactForAlarmInMiliseconds
import de.coldtea.smplr.smplralarm.models.NotificationChannelItem
import de.coldtea.smplr.smplralarm.models.NotificationItem
import de.coldtea.smplr.smplralarm.receivers.AlarmNotification
import de.coldtea.smplr.smplralarm.receivers.AlarmReceiver
import de.coldtea.smplr.smplralarm.receivers.SmplrAlarmReceiverObjects.Companion.SMPLR_ALARM_RECEIVER_INTENT_ID
import de.coldtea.smplr.smplralarm.receivers.SmplrAlarmReceiverObjects.Companion.alarmNotification
import de.coldtea.smplr.smplralarm.models.ActiveWeekDays
import de.coldtea.smplr.smplralarm.models.WeekDays
import de.coldtea.smplr.smplralarm.repository.AlarmNotificationRepository
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.Calendar
import java.util.concurrent.TimeUnit

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

    fun weekdays(lambda: WeekDaysManager.() -> Unit){
        weekdays = WeekDaysManager().apply(lambda).getWeekDays()
    }

    // endregion

    // region functionalities

    fun setAlarm(): Int {

        val calendar = Calendar.getInstance()
        requestCode = calendar.getTimeExactForAlarmInMiliseconds(hour, min, weekdays).toInt()
        Timber.v("SmplrAlarm.AlarmManager.setAlarm: $requestCode -- $hour:$min")

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            AlarmReceiver.build(context).putExtra(SMPLR_ALARM_RECEIVER_INTENT_ID, requestCode),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notifiactionBuilderItem = AlarmNotification(
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

        CoroutineScope(Dispatchers.IO).launch {
            alarmNotificationRepository.insertAlarmNotification(notifiactionBuilderItem)
        }

        alarmNotification.add(notifiactionBuilderItem)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.getTimeExactForAlarmInMiliseconds(
                hour,
                min,
                weekdays
            ),
            pendingIntent
        )

        return requestCode
    }

    fun cancelAlarm() {
        Timber.v("SmplrAlarm.AlarmManager.cancelAlarm: $requestCode -- $hour:$min")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE
        )

        alarmManager.cancel(pendingIntent)

        CoroutineScope(Dispatchers.IO).launch {
            alarmNotificationRepository.deleteAlarmNotification(requestCode)
        }
    }

    private fun Int.setAlarmIn() = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(toLong())

    // endregion

    // region companion

    companion object {
        private const val DUMMY_ALARM_DURATION = 5
    }

    // endregion
}
