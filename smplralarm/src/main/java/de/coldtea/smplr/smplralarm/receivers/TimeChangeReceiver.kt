package de.coldtea.smplr.smplralarm.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.coldtea.smplr.smplralarm.repository.AlarmNotificationRepository
import de.coldtea.smplr.smplralarm.services.AlarmService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by [Yasar Naci Gündüz](https://github.com/ColdTea-Projects?tab=following).
 */

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
                val alarmService = AlarmService(context)

                cancelAndResetAlarmNotifications(alarmService, alarmNotifications)
            }

        } catch (e: Exception) {
            Timber.e(e.toString())
        }

    private fun cancelAndResetAlarmNotifications(
        alarmService: AlarmService,
        alarmNotifications: List<AlarmNotification>
    ) =
        alarmNotifications.map { alarmNotification -> alarmService.renewAlarm(alarmNotification) }

    companion object {
        fun build(context: Context): Intent {
            return Intent(context, TimeChangeReceiver::class.java)
        }
    }

}