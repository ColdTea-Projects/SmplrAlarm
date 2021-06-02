package de.coldtea.smplr.smplralarm.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.coldtea.smplr.smplralarm.extensions.getFullScreenIntent
import de.coldtea.smplr.smplralarm.repository.AlarmNotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by [Yasar Naci Gündüz](https://github.com/ColdTea-Projects).
 */

internal class ActivateAppReceiver : BroadcastReceiver() {
    private var repository: AlarmNotificationRepository? = null

    override fun onReceive(context: Context, intent: Intent) {
        val requestId =
            intent.getIntExtra(SmplrAlarmReceiverObjects.SMPLR_ALARM_RECEIVER_INTENT_ID, -1)


        onAlarmIndicatorTapped(context, requestId)
    }

    private fun onAlarmIndicatorTapped(context: Context, requestId: Int) = CoroutineScope(Dispatchers.IO).launch {
        repository = AlarmNotificationRepository(context)
        repository?.let {
            val alarmNotification = it.getAlarmNotification(requestId)
            if(alarmNotification.intent == null) return@let
            context.getFullScreenIntent(alarmNotification.intent)
        }
    }

}