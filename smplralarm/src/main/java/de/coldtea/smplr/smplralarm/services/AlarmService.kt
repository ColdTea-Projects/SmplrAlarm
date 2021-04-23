package de.coldtea.smplr.smplralarm.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import de.coldtea.smplr.smplralarm.extensions.getTimeExactForAlarmInMilliseconds
import de.coldtea.smplr.smplralarm.models.WeekDays
import de.coldtea.smplr.smplralarm.receivers.AlarmNotification
import de.coldtea.smplr.smplralarm.receivers.AlarmReceiver
import de.coldtea.smplr.smplralarm.receivers.SmplrAlarmReceiverObjects
import timber.log.Timber
import java.util.*

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
        pendingIntent: PendingIntent? = null
    ) {
        val intent = pendingIntent ?: createPendingIntent(requestCode, 0)
        val exactAlarmTime =
            calendar.getTimeExactForAlarmInMilliseconds(hour, min, weekDays)

        val alarmClockInfo = AlarmManager.AlarmClockInfo(
            exactAlarmTime,
            intent
        )

        alarmManager.setAlarmClock(
            alarmClockInfo,
            intent
        )
    }

    fun setAlarm(
        alarmNotification: AlarmNotification
    ) {
        setAlarm(
            requestCode = alarmNotification.alarmNotificationId,
            hour= alarmNotification.hour,
            min = alarmNotification.min,
            weekDays = alarmNotification.weekDays
        )
    }

    fun resetAlarmTomorrow(alarmNotification: AlarmNotification) {
        val pendingIntent = getPendingIntent(
            alarmNotification.alarmNotificationId,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        setAlarm(
            requestCode = alarmNotification.alarmNotificationId,
            hour= alarmNotification.hour,
            min = alarmNotification.min,
            weekDays = alarmNotification.weekDays,
            pendingIntent = pendingIntent
        )
    }

    fun renewAlarm(alarmNotification: AlarmNotification){
        cancelAlarm(alarmNotification.alarmNotificationId)

        val pendingIntent = createPendingIntent(alarmNotification.alarmNotificationId, PendingIntent.FLAG_UPDATE_CURRENT)

        setAlarm(
            requestCode = alarmNotification.alarmNotificationId,
            hour= alarmNotification.hour,
            min = alarmNotification.min,
            weekDays = alarmNotification.weekDays,
            pendingIntent = pendingIntent
        )

    }

    fun alarmExist(requestCode: Int): Boolean =
        getPendingIntent(requestCode, PendingIntent.FLAG_NO_CREATE) != null

    fun cancelAlarm(requestCode: Int) {
        val pendingIntent = getPendingIntent(requestCode, PendingIntent.FLAG_NO_CREATE) ?: return
        alarmManager.cancel(pendingIntent)
    }

    private fun createPendingIntent(requestCode: Int, flag: Int) = PendingIntent.getBroadcast(
        context,
        requestCode,
        Intent(context, AlarmReceiver::class.java).putExtra(SmplrAlarmReceiverObjects.SMPLR_ALARM_RECEIVER_INTENT_ID, requestCode),
        flag
    )

    private fun getPendingIntent(requestCode: Int, flag: Int) = PendingIntent.getBroadcast(
        context,
        requestCode,
        Intent(context, AlarmReceiver::class.java).putExtra(
            SmplrAlarmReceiverObjects.SMPLR_ALARM_RECEIVER_INTENT_ID,
            requestCode
        ),
        flag
    )
}