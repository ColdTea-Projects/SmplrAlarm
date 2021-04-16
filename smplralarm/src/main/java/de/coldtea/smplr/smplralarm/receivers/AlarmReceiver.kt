package de.coldtea.smplr.smplralarm.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.coldtea.smplr.smplralarm.alarmlogs.LogsRepository
import de.coldtea.smplr.smplralarm.alarmlogs.RangAlarmObject
import de.coldtea.smplr.smplralarm.extensions.getTimeExactForAlarmInMilliseconds
import de.coldtea.smplr.smplralarm.extensions.showNotificationWithIntent
import de.coldtea.smplr.smplralarm.models.IntentNotificationItem
import de.coldtea.smplr.smplralarm.repository.AlarmNotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.lang.IllegalArgumentException
import java.util.*

internal class AlarmReceiver : BroadcastReceiver() {
    private var repository: AlarmNotificationRepository? = null


    override fun onReceive(context: Context, intent: Intent) {
        val now = Calendar.getInstance().dateTime()
        val logsRepository = LogsRepository(context.applicationContext)

        try {
            repository = AlarmNotificationRepository(context)


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
                            resetTheAlarmForTheNextDayOnTheList(context, alarmNotification)
                    } catch (ex: IllegalArgumentException) {
                        Timber.e("SmplrAlarmApp.SmplrAlarmManager.updateRepeatingAlarm: The alarm intended to be removed does not exist! ")

                        logsRepository.logAlarm(
                            RangAlarmObject(
                                "${now.first} - ${now.second}",
                                ex.toString()
                            )
                        )
                    } catch (ex: Exception) {
                        Timber.e("SmplrAlarmApp.SmplrAlarmManager.updateRepeatingAlarm: $ex ")
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
            Timber.e("SmplrAlarm.AlarmReceiver.onReceive: exception --> $ex")
            logsRepository.logAlarm(
                RangAlarmObject(
                    "${now.first} - ${now.second}",
                    ex.toString()
                )
            )
        }
    }

    private fun resetTheAlarmForTheNextDayOnTheList(
        context: Context,
        alarmNotification: AlarmNotification
    ) = repository?.let {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
        val pendingIntent = createPendingIntent(context, alarmNotification)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.getTimeExactForAlarmInMilliseconds(
                alarmNotification.hour,
                alarmNotification.min,
                alarmNotification.weekDays,
                1
            ),
            pendingIntent
        )

    }

    private fun createPendingIntent(context: Context, alarmNotification: AlarmNotification) =
        PendingIntent.getBroadcast(
            context,
            alarmNotification.alarmNotificationId,
            build(context).putExtra(
                SmplrAlarmReceiverObjects.SMPLR_ALARM_RECEIVER_INTENT_ID,
                alarmNotification.alarmNotificationId
            ),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

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