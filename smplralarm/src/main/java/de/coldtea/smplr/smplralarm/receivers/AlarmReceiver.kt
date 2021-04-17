package de.coldtea.smplr.smplralarm.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.coldtea.smplr.smplralarm.extensions.showNotificationWithIntent
import de.coldtea.smplr.smplralarm.models.IntentNotificationItem
import de.coldtea.smplr.smplralarm.repository.AlarmNotificationRepository
import de.coldtea.smplr.smplralarm.services.AlarmService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.IllegalArgumentException

internal class AlarmReceiver : BroadcastReceiver() {
    private var repository: AlarmNotificationRepository? = null

    override fun onReceive(context: Context, intent: Intent) {
        try {
            repository = AlarmNotificationRepository(context)
            val alarmService = AlarmService(context)
            val requestId =
                intent.getIntExtra(SmplrAlarmReceiverObjects.SMPLR_ALARM_RECEIVER_INTENT_ID, -1)

            Timber.v("SmplrAlarm.AlarmReceiver.onReceive --> $requestId")

            if (requestId == -1) return

            CoroutineScope(Dispatchers.IO).launch {

                repository?.let {
                    try {
                        val alarmNotification = it.getAlarmNotification(requestId)

                        context.showNotificationWithIntent(
                            alarmNotification.notificationChannelItem,
                            IntentNotificationItem(
                                alarmNotification.fullScreenIntent,
                                alarmNotification.notificationItem
                            )
                        )

                        if (alarmNotification.weekDays.isNullOrEmpty())
                            it.deactivateSingleAlarmNotification(requestId)
                        else
                            alarmService.resetAlarmTomorrow(alarmNotification)
                    } catch (ex: IllegalArgumentException) {
                        Timber.e("SmplrAlarmApp.SmplrAlarmManager.updateRepeatingAlarm: The alarm intended to be removed does not exist! ")
                    } catch (ex: Exception) {
                        Timber.e("SmplrAlarmApp.SmplrAlarmManager.updateRepeatingAlarm: $ex ")
                    }

                }

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