package de.coldtea.smplr.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmAPI.Companion.SMPLR_ALARM_REQUEST_ID
import timber.log.Timber

class AlarmBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val requestId = intent.getIntExtra(SMPLR_ALARM_REQUEST_ID, -1)

        Timber.i("SmplrAlarm received with id: $requestId")
    }
}