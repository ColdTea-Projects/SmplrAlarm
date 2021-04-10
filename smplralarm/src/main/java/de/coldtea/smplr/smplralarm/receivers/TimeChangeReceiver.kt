package de.coldtea.smplr.smplralarm.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.coldtea.smplr.smplralarm.extensions.getTimeExactForAlarmInMilliseconds
import de.coldtea.smplr.smplralarm.repository.AlarmNotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

internal class TimeChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Timber.i("SmplrAlarm.TimeChangeReceiver.onRecieve --> ${intent.action}")
        when (intent.action) {
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_DATE_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED -> onBootComplete(context)
            else -> Timber.w("SmplrAlarm --> Recieved illegal broadcast!")
        }
    }

    private fun onBootComplete(context: Context) =
        try {

            CoroutineScope(Dispatchers.IO).launch {
                val notificationRepository = AlarmNotificationRepository(context)
                val alarmNotifications = notificationRepository.getAllAlarmNotifications()

                cancelAndResetAlarmNotifications(context, alarmNotifications)

                notificationRepository.deleteAlarmsBeforeNow()
            }

        } catch (e: Exception) {
            Timber.e(e.toString())
        }

    private fun cancelAndResetAlarmNotifications(
        context: Context,
        alarmNotifications: List<AlarmNotification>
    ) =
        alarmNotifications.map {

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val calendar = Calendar.getInstance()

            val pendingIntentToCancel = getPendingIntent(context, it.alarmNotificationId)

            alarmManager.cancel(pendingIntentToCancel)

            val pendingIntent = createPendingIntent(context, it.alarmNotificationId)

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeExactForAlarmInMilliseconds(
                    it.hour,
                    it.min,
                    it.weekDays
                ),
                pendingIntent
            )

        }


    private fun createPendingIntent(context: Context, alarmNotificationId: Int) =
        PendingIntent.getBroadcast(
            context,
            alarmNotificationId,
            AlarmReceiver.build(context).putExtra(
                SmplrAlarmReceiverObjects.SMPLR_ALARM_RECEIVER_INTENT_ID,
                alarmNotificationId
            ),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    private fun getPendingIntent(context: Context, alarmNotificationId: Int) =
        PendingIntent.getBroadcast(
            context,
            alarmNotificationId,
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE
        )

    companion object {
        fun build(context: Context): Intent {
            return Intent(context, TimeChangeReceiver::class.java)
        }
    }

}