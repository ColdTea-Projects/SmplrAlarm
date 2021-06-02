package de.coldtea.smplr.smplralarm.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.coldtea.smplr.smplralarm.alarmlogs.LogsRepository
import de.coldtea.smplr.smplralarm.alarmlogs.RangAlarmObject
import de.coldtea.smplr.smplralarm.extensions.showNotification
import de.coldtea.smplr.smplralarm.repository.AlarmNotificationRepository
import de.coldtea.smplr.smplralarm.services.AlarmService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by [Yasar Naci Gündüz](https://github.com/ColdTea-Projects).
 */

internal class AlarmReceiver : BroadcastReceiver() {
    private var repository: AlarmNotificationRepository? = null

    override fun onReceive(context: Context, intent: Intent) {
        val requestId =
            intent.getIntExtra(SmplrAlarmReceiverObjects.SMPLR_ALARM_RECEIVER_INTENT_ID, -1)

        onAlarmReceived(context, requestId)
    }

    private fun onAlarmReceived(context: Context, requestId: Int){
        val now = Calendar.getInstance().dateTime()
        val logsRepository = LogsRepository(context.applicationContext)

        try {
            repository = AlarmNotificationRepository(context)
            val alarmService = AlarmService(context)

            Timber.v("onReceive --> $requestId")

            if (requestId == -1) return

            CoroutineScope(Dispatchers.IO).launch {

                repository?.let {
                    try {
                        val alarmNotification = it.getAlarmNotification(requestId)

                        if(alarmNotification.fullScreenIntent == null){
                            context.showNotification(
                                requestId,
                                alarmNotification.notificationChannelItem,
                                alarmNotification.notificationItem
                            )
                        }else{
                            context.showNotification(
                                requestId,
                                alarmNotification.notificationChannelItem,
                                alarmNotification.notificationItem,
                                alarmNotification.fullScreenIntent
                            )
                        }

                        if (alarmNotification.weekDays.isNullOrEmpty())
                            it.deactivateSingleAlarmNotification(requestId)
                        else
                            alarmService.resetAlarmTomorrow(alarmNotification)
                    } catch (ex: IllegalArgumentException) {
                        Timber.e("updateRepeatingAlarm: The alarm intended to be removed does not exist! ")

                        logsRepository.logAlarm(
                            RangAlarmObject(
                                "${now.first} - ${now.second}",
                                ex.toString()
                            )
                        )
                    } catch (ex: Exception) {
                        Timber.e("updateRepeatingAlarm: $ex ")
                        logsRepository.logAlarm(
                            RangAlarmObject(
                                "${now.first} - ${now.second}",
                                ex.toString()
                            )
                        )
                    }
                }

            }

            logsRepository.logAlarm(
                RangAlarmObject(
                    "${now.first} - ${now.second}",
                    ALARM_RECEIVER_SUCCESS
                )
            )

        } catch (ex: Exception) {
            Timber.e("onReceive: exception --> $ex")
            logsRepository.logAlarm(
                RangAlarmObject(
                    "${now.first} - ${now.second}",
                    ex.toString()
                )
            )
        }
    }

    private fun Calendar.dateTime(): Pair<String, String> {
        val sdfDate = SimpleDateFormat("dd/M/yyyy", Locale.getDefault())
        val sdfTime = SimpleDateFormat("hh:mm:ss", Locale.getDefault())

        return sdfDate.format(time) to sdfTime.format(time)
    }

    companion object {
        private const val ALARM_RECEIVER_SUCCESS = "Alarm receiver worked successfully"

        fun build(context: Context): Intent {
            return Intent(context, AlarmReceiver::class.java)
        }
    }
}