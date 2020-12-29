package de.coldtea.smplr.smplralarm.managers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import de.coldtea.smplr.smplralarm.extensions.getTimeExactForAlarmInMiliseconds
import de.coldtea.smplr.smplralarm.models.NotificationChannelItem
import de.coldtea.smplr.smplralarm.models.NotificationItem
import de.coldtea.smplr.smplralarm.receivers.AlarmNotification
import de.coldtea.smplr.smplralarm.receivers.SmplrAlarmReceiver
import de.coldtea.smplr.smplralarm.receivers.SmplrAlarmReceiverObjects.Companion.SMPLR_ALARM_RECEIVER_INTENT_ID
import de.coldtea.smplr.smplralarm.receivers.SmplrAlarmReceiverObjects.Companion.alarmNotification
import java.util.Calendar
import java.util.concurrent.TimeUnit


class AlarmManager(val context: Context) {

    //region properties

    var hour = -1
    var min = -1
    var requestCode = -1
    var intent: Intent? = null
    var fullScreenIntent: Intent? = null

    var notificationChannel: NotificationChannelItem? = null
    var notification: NotificationItem? = null

    //endregion

    // region computed properties

    private val alarmManager: AlarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
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

    // endregion

    // region functionalities

    fun setAlarm(): Int {

        val calendar = Calendar.getInstance()
        requestCode = calendar.getTimeExactForAlarmInMiliseconds(hour, min).toInt()

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            SmplrAlarmReceiver.build(context).putExtra(SMPLR_ALARM_RECEIVER_INTENT_ID, requestCode),
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        val notifiactionBuilderItem = AlarmNotification(
            requestCode,
            notificationChannel
                ?: ChannelManager().build(),
            notification
                ?: AlarmNotificationManager().build(),
            intent,
            fullScreenIntent
            )

        alarmNotification.add(notifiactionBuilderItem)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.getTimeExactForAlarmInMiliseconds(hour, min),//DUMMY_ALARM_DURATION.setAlarmIn(),//
            pendingIntent
        )

        return requestCode

    }

    fun cancelAlarm() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            Intent(context, SmplrAlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE
        )

        alarmManager.cancel(pendingIntent)
    }

    private fun Int.setAlarmIn() = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(toLong())

    // endregion

    // region companion

    companion object {
        private const val DUMMY_ALARM_DURATION = 5
    }

    // endregion
}
