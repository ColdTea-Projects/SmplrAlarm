package de.coldtea.smplr.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmAPI.Companion.SMPLR_ALARM_NOTIFICATION_ID
import timber.log.Timber

class NotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(SMPLR_ALARM_NOTIFICATION_ID, -1)

        Timber.i("SmplrAlarm notification received with id: $notificationId")
    }
}