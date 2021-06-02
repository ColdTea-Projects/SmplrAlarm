package de.coldtea.smplr.alarm.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.coldtea.smplr.alarm.alarms.AlarmViewModel.Companion.ACTION_DISMISS
import de.coldtea.smplr.alarm.alarms.AlarmViewModel.Companion.ACTION_SNOOZE
import de.coldtea.smplr.alarm.alarms.AlarmViewModel.Companion.HOUR
import de.coldtea.smplr.alarm.alarms.AlarmViewModel.Companion.MINUTE
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmAPI.Companion.SMPLR_ALARM_NOTIFICATION_ID
import de.coldtea.smplr.smplralarm.smplrAlarmSet
import java.util.*

class ActionReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(SMPLR_ALARM_NOTIFICATION_ID, -1)
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val now = getHourAndMinute()

        if(intent.action == ACTION_SNOOZE){
            val updatedTime = addOneMinute(intent.getIntExtra(HOUR, now.first), intent.getIntExtra(MINUTE, now.second))

            notificationManager.cancel(notificationId)

            smplrAlarmSet(context){
                hour { updatedTime.first }
                min { updatedTime.second }
            }
        }
        if (intent.action == ACTION_DISMISS){
            notificationManager.cancel(notificationId)
        }
    }

    private fun addOneMinute(hour: Int, minute: Int): Pair<Int, Int>{
        var mMinute = minute + 1
        var mHour = hour

        if(mMinute == 60){
            mMinute -= 60
            mHour += 1
        }

        if (mHour > 23)
            mHour = 0

        return mHour to mMinute
    }

    private fun getHourAndMinute(): Pair<Int, Int> = Calendar.getInstance().let {
            it.get(Calendar.HOUR_OF_DAY) to it.get(Calendar.MINUTE)
        }
}