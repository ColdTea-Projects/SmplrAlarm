package de.coldtea.smplr.smplralarm.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.coldtea.smplr.smplralarm.extensions.showNotification
import de.coldtea.smplr.smplralarm.extensions.showNotificationWithIntent
import de.coldtea.smplr.smplralarm.managers.AlarmNotificationManager
import de.coldtea.smplr.smplralarm.managers.ChannelManager
import de.coldtea.smplr.smplralarm.models.IntentNotificationItem
import de.coldtea.smplr.smplralarm.models.NotificationChannelItem
import de.coldtea.smplr.smplralarm.models.NotificationItem
import de.coldtea.smplr.smplralarm.repository.AlarmNotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

internal class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
            try {
                val repository = AlarmNotificationRepository(context)
                val requestId = intent.getIntExtra(SmplrAlarmReceiverObjects.SMPLR_ALARM_RECEIVER_INTENT_ID, -1)

                Timber.v("SmplrAlarm.AlarmReceiver.onReceive --> $requestId")

                if(requestId == -1) return

                CoroutineScope(Dispatchers.IO).launch {
                    val alarmNotification = repository.getAlarmNotification(requestId)

                    context.showNotificationWithIntent(
                        alarmNotification.notificationChannelItem,
                        IntentNotificationItem(
                            alarmNotification.fullScreenIntent,
                            alarmNotification.notificationItem
                        )
                    )

                    repository.deleteAlarmNotification(requestId)
                }
            } catch (e: Exception) {
                Timber.e("SmplrAlarm.AlarmReceiver.onReceive: exception --> $e")
            }
    }

    companion object {
        fun build(context: Context): Intent {
            return Intent(context, AlarmReceiver::class.java)
        }
    }

}