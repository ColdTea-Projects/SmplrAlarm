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

internal class RebootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Timber.i("SmplrAlarm.RebootReceiver.onRecieve --> ${intent.action}")
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED -> onBootComplete(context)
            else -> Timber.w("SmplrAlarm --> Recieved illegal broadcast!")
        }
    }

    private fun onBootComplete(context: Context) =
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val calendar = Calendar.getInstance()

            CoroutineScope(Dispatchers.IO).launch {
                val notificationRepository = AlarmNotificationRepository(context)
                val alarmNotifications = notificationRepository.getAllAlarmNotifications()

                alarmNotifications.filter { it.isActive }.map {
                    val pendingIntent = createPendingIntent(context, it)

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

                notificationRepository.deleteAlarmsBeforeNow()
            }

        } catch (e: Exception) {
            Timber.e(e.toString())
        }

    private fun createPendingIntent(context: Context, alarmNotification: AlarmNotification) =
        PendingIntent.getBroadcast(
            context,
            alarmNotification.alarmNotificationId,
            AlarmReceiver.build(context).putExtra(
                SmplrAlarmReceiverObjects.SMPLR_ALARM_RECEIVER_INTENT_ID,
                alarmNotification.alarmNotificationId
            ),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    companion object {
        fun build(context: Context): Intent {
            return Intent(context, RebootReceiver::class.java)
        }
    }

}