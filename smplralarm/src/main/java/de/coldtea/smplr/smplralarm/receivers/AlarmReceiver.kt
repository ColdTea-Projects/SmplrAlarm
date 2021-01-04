package de.coldtea.smplr.smplralarm.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.coldtea.smplr.smplralarm.extensions.showNotification
import de.coldtea.smplr.smplralarm.managers.AlarmNotificationManager
import de.coldtea.smplr.smplralarm.managers.ChannelManager
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

                Timber.v("SmplrAlarm.AlarmReceiver.onReceive: $requestId")

                if(requestId == -1) return

                CoroutineScope(Dispatchers.IO).launch {
                    repository.deleteAlarmNotification(requestId)
                }

                context.showNotification(
                    NotificationChannelItem(
                        NotificationManager.IMPORTANCE_HIGH,
                        true,
                        ChannelManager.SMPLR_ALARM_CHANNEL_DEFAULT_NAME,
                        ChannelManager.SMPLR_ALARM_CHANNEL_DEFAULT_DESCRIPTION
                    ),
                    NotificationItem(
                        android.R.drawable.ic_lock_idle_alarm,
                        AlarmNotificationManager.SMPLR_ALARM_CHANNEL_DEFAULT_TITLE,
                        AlarmNotificationManager.SMPLR_ALARM_CHANNEL_DEFAULT_MESSAGE,
                        AlarmNotificationManager.SMPLR_ALARM_CHANNEL_DEFAULT_BIG_TEXT,
                        true
                    )
                )
//
//                alarmNotification.find {
//                    it.alarmNotificationId == intent.getIntExtra(SMPLR_ALARM_RECEIVER_INTENT_ID, 0)
//                }?.let {
//
//                    it.alarmRingEvent?.invoke(it.alarmNotificationId)
//
//                    if (it.fullScreenIntent != null){
//                        val cal = Calendar.getInstance()
//                        Timber.i("SmplrAlarm.SmplrAlarmReceiver.fullScreenIntent: ${it.alarmNotificationId} -- ${cal.get(Calendar.HOUR_OF_DAY)}:${cal.get(Calendar.MINUTE)}")
//                        context.showNotificationWithIntent(
//                            it.notificationChannelItem,
//                            IntentNotificationItem(it.fullScreenIntent, it.notificationItem)
//                        )
//                    }
//
//                }
            } catch (e: Exception) {
                Timber.e("SmplrAlarm.AlarmReceiver.onReceive: $e")
            }
    }

    companion object {
        fun build(context: Context): Intent {
            return Intent(context, AlarmReceiver::class.java)
        }
    }

}