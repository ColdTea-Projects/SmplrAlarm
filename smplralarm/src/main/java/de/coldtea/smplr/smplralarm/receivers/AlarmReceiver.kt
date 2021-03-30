package de.coldtea.smplr.smplralarm.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.coldtea.smplr.smplralarm.extensions.getTimeExactForAlarmInMilliseconds
import de.coldtea.smplr.smplralarm.extensions.showNotificationWithIntent
import de.coldtea.smplr.smplralarm.models.IntentNotificationItem
import de.coldtea.smplr.smplralarm.repository.AlarmNotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

internal class AlarmReceiver : BroadcastReceiver() {
    private var repository: AlarmNotificationRepository? = null

    override fun onReceive(context: Context, intent: Intent) {
            try {
                repository = AlarmNotificationRepository(context)
                val requestId = intent.getIntExtra(SmplrAlarmReceiverObjects.SMPLR_ALARM_RECEIVER_INTENT_ID, -1)

                Timber.v("SmplrAlarm.AlarmReceiver.onReceive --> $requestId")

                if(requestId == -1) return

                CoroutineScope(Dispatchers.IO).launch {

                    repository?.let {

                        val alarmNotification = it.getAlarmNotification(requestId)

                        context.showNotificationWithIntent(
                            alarmNotification.notificationChannelItem,
                            IntentNotificationItem(
                                alarmNotification.fullScreenIntent,
                                alarmNotification.notificationItem
                            )
                        )

                        if(!it.deleteAlarmNotificationWithResult(requestId)){
                            resetTheAlarmForTheNextDayOnTheList(context, alarmNotification)
                        }

                    }

                }
            } catch (e: Exception) {
                Timber.e("SmplrAlarm.AlarmReceiver.onReceive: exception --> $e")
            }
    }

    private fun resetTheAlarmForTheNextDayOnTheList(context: Context, alarmNotification: AlarmNotification) = repository?.let{
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
        val pendingIntent = createPendingIntent(context, alarmNotification)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.getTimeExactForAlarmInMilliseconds(
                alarmNotification.hour,
                alarmNotification.min,
                alarmNotification.weekDays,
                1
            ),
            pendingIntent
        )

    }

    private fun createPendingIntent(context: Context, alarmNotification: AlarmNotification) = PendingIntent.getBroadcast(
        context,
        alarmNotification.alarmNotificationId,
        build(context).putExtra(
            SmplrAlarmReceiverObjects.SMPLR_ALARM_RECEIVER_INTENT_ID,
            alarmNotification.alarmNotificationId
        ),
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    companion object {
        fun build(context: Context): Intent {
            return Intent(context, AlarmReceiver::class.java)
        }
    }

}