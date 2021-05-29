package de.coldtea.smplr.smplralarm.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import de.coldtea.smplr.smplralarm.extensions.getTimeExactForAlarmInMilliseconds
import de.coldtea.smplr.smplralarm.models.WeekDays
import de.coldtea.smplr.smplralarm.receivers.ActivateAppReceiver
import de.coldtea.smplr.smplralarm.receivers.AlarmNotification
import de.coldtea.smplr.smplralarm.receivers.AlarmReceiver
import de.coldtea.smplr.smplralarm.receivers.SmplrAlarmReceiverObjects
import java.util.*

/**
 * Created by [Yasar Naci Gündüz](https://github.com/ColdTea-Projects?tab=following).
 */
class AlarmService(val context: Context) {

    private val alarmManager: AlarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private val calendar
        get() = Calendar.getInstance()

    fun setAlarm(
        requestCode: Int,
        hour: Int,
        min: Int,
        weekDays: List<WeekDays>,
        receiverIntent: PendingIntent? = null
    ) {
        val alarmReceiverIntent = receiverIntent ?: createReceiverPendingIntent(requestCode, 0)
        val openAppIntent = createOpenAppPendingIntent(requestCode, 0)
        val exactAlarmTime =
            calendar.getTimeExactForAlarmInMilliseconds(hour, min, weekDays)

        val alarmClockInfo = AlarmManager.AlarmClockInfo(
            exactAlarmTime,
            openAppIntent
        )

        alarmManager.setAlarmClock(
            alarmClockInfo,
            alarmReceiverIntent
        )
    }

    fun setAlarm(
        alarmNotification: AlarmNotification
    ) {
        setAlarm(
            requestCode = alarmNotification.alarmNotificationId,
            hour = alarmNotification.hour,
            min = alarmNotification.min,
            weekDays = alarmNotification.weekDays
        )
    }

    fun resetAlarmTomorrow(alarmNotification: AlarmNotification) {
        val pendingIntent = getReceiverPendingIntent(
            alarmNotification.alarmNotificationId,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        setAlarm(
            requestCode = alarmNotification.alarmNotificationId,
            hour = alarmNotification.hour,
            min = alarmNotification.min,
            weekDays = alarmNotification.weekDays,
            receiverIntent = pendingIntent
        )
    }

    fun renewAlarm(alarmNotification: AlarmNotification) {
        cancelAlarm(alarmNotification.alarmNotificationId)

        val pendingIntent = createReceiverPendingIntent(
            alarmNotification.alarmNotificationId,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        setAlarm(
            requestCode = alarmNotification.alarmNotificationId,
            hour = alarmNotification.hour,
            min = alarmNotification.min,
            weekDays = alarmNotification.weekDays,
            receiverIntent = pendingIntent
        )

    }

    fun alarmExist(requestCode: Int): Boolean =
        getReceiverPendingIntent(requestCode, PendingIntent.FLAG_NO_CREATE) != null

    fun cancelAlarm(requestCode: Int) {
        val pendingIntent =
            getReceiverPendingIntent(requestCode, PendingIntent.FLAG_NO_CREATE) ?: return
        alarmManager.cancel(pendingIntent)
    }

    private fun createReceiverPendingIntent(requestCode: Int, flag: Int) =
        PendingIntent.getBroadcast(
            context,
            requestCode,
            Intent(
                context,
                AlarmReceiver::class.java
            ).putExtra(SmplrAlarmReceiverObjects.SMPLR_ALARM_RECEIVER_INTENT_ID, requestCode),
            flag
        )

    private fun getReceiverPendingIntent(requestCode: Int, flag: Int) = PendingIntent.getBroadcast(
        context,
        requestCode,
        Intent(context, AlarmReceiver::class.java).putExtra(
            SmplrAlarmReceiverObjects.SMPLR_ALARM_RECEIVER_INTENT_ID,
            requestCode
        ),
        flag
    )

    private fun createOpenAppPendingIntent(requestCode: Int, flag: Int) =
        PendingIntent.getBroadcast(
            context,
            requestCode,
            Intent(
                context,
                ActivateAppReceiver::class.java
            ).putExtra(
                SmplrAlarmReceiverObjects.SMPLR_ALARM_RECEIVER_INTENT_ID,
                requestCode
            ),
            flag
        )
}