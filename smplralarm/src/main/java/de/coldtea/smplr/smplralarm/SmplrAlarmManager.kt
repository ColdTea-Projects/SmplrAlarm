package de.coldtea.smplr.smplralarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import de.coldtea.smplr.smplralarm.extensions.getTimeExactForAlarmInMiliseconds
import de.coldtea.smplr.smplralarm.receivers.NotificationBuilderItem
import de.coldtea.smplr.smplralarm.receivers.SmplrAlarmReceiver
import de.coldtea.smplr.smplralarm.receivers.SmplrAlarmReceiverObjects.Companion.SMPLR_ALARM_RECEIVER_INTENT_ID
import de.coldtea.smplr.smplralarm.receivers.SmplrAlarmReceiverObjects.Companion.notificationBuilders
import java.util.Calendar
import java.util.concurrent.TimeUnit


class SmplrAlarmManager(val context: Context) {

    //region properties

    var hour = -1
    var min = -1
    var requestCode = -1
    var notificationBuilder: NotificationCompat.Builder? = null
    var intent : Intent? = null

    //endregion

    // region computed properties

    private val alarmManager: AlarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    // endregion

    // region DSL setters
    fun hour(hour: () -> Int) {
        this.hour = hour()
    }

    fun min(min: () -> Int) {
        this.min = min()
    }

    fun notificationBuilder(notificationBuilder: () -> NotificationCompat.Builder) {
        this.notificationBuilder = notificationBuilder()
    }

    fun requestCode(requestCode: () -> Int) {
        this.requestCode = requestCode()
    }

    fun intent(intent: () -> Intent) {
        this.intent = intent()
    }


    // endregion

    // region functionalities

    fun setAlarm(): Int {

        val calendar = Calendar.getInstance()
        requestCode = calendar.getTimeExactForAlarmInMiliseconds(hour, min).toInt()
        val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                SmplrAlarmReceiver.build(context).putExtra(SMPLR_ALARM_RECEIVER_INTENT_ID, requestCode),
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notifiactionBuilderItem = NotificationBuilderItem(
                requestCode,
                this.notificationBuilder as NotificationCompat.Builder,
                intent as Intent
        )

        notificationBuilders.add(notifiactionBuilderItem)

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                DUMMY_ALARM_DURATION.setAlarmIn(),//calendar.getTimeExactForAlarmInMiliseconds(hour, min),
                pendingIntent
        )

        return requestCode
    }

    fun cancelAlarm() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                Intent(context, SmplrAlarmReceiver::class.java),
                PendingIntent.FLAG_NO_CREATE
        )

        alarmManager.cancel(pendingIntent)
    }

    private fun Int.setAlarmIn() = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(toLong())

    // endregion

    // region companion

    companion object {
        private const val DUMMY_ALARM_DURATION = 5
    }

    // endregion
}
