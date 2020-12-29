package de.coldtea.smplr.smplralarm.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.coldtea.smplr.smplralarm.extensions.showNotificationWithIntent
import de.coldtea.smplr.smplralarm.models.IntentNotificationItem
import de.coldtea.smplr.smplralarm.models.NotificationChannelItem
import de.coldtea.smplr.smplralarm.receivers.SmplrAlarmReceiverObjects.Companion.SMPLR_ALARM_RECEIVER_INTENT_ID
import de.coldtea.smplr.smplralarm.receivers.SmplrAlarmReceiverObjects.Companion.alarmNotification
import timber.log.Timber
import java.util.*

internal class SmplrAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
            try {
                alarmNotification.find {
                    it.alarmNotificationId == intent.getIntExtra(SMPLR_ALARM_RECEIVER_INTENT_ID, 0)
                }?.let {

                    it.alarmRingEvent?.invoke(it.alarmNotificationId)

                    if (it.fullScreenIntent != null){
                        val cal = Calendar.getInstance()
                        Timber.i("SmplrAlarm.SmplrAlarmReceiver.fullScreenIntent: ${it.alarmNotificationId} -- ${cal.get(Calendar.HOUR_OF_DAY)}:${cal.get(Calendar.MINUTE)}")
                        context.showNotificationWithIntent(
                            it.notificationChannelItem,
                            IntentNotificationItem(it.fullScreenIntent, it.notificationItem)
                        )
                    }

                }
            } catch (e: Exception) {
                Timber.e(e.toString())
            }
    }

    companion object {
        fun build(context: Context): Intent {
            return Intent(context, SmplrAlarmReceiver::class.java)
        }
    }

}