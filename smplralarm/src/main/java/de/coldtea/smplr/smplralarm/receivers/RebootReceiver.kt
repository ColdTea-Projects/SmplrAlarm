package de.coldtea.smplr.smplralarm.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.coldtea.smplr.smplralarm.repository.AlarmNotificationRepository
import de.coldtea.smplr.smplralarm.services.AlarmService
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
            val alarmService = AlarmService(context)

            CoroutineScope(Dispatchers.IO).launch {
                val notificationRepository = AlarmNotificationRepository(context)
                val alarmNotifications = notificationRepository.getAllAlarmNotifications()

                alarmNotifications.filter { it.isActive }.map {
                    alarmService.setAlarm(it)
                }

                notificationRepository.deleteAlarmsBeforeNow()
            }

        } catch (e: Exception) {
            Timber.e(e.toString())
        }


    companion object {
        fun build(context: Context): Intent {
            return Intent(context, RebootReceiver::class.java)
        }
    }

}