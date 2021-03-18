package de.coldtea.smplr.alarm.alarms

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import de.coldtea.smplr.alarm.alarms.models.AlarmInfo
import de.coldtea.smplr.alarm.alarms.models.WeekInfo
import de.coldtea.smplr.alarm.extensions.getTimeFormattedString
import de.coldtea.smplr.alarm.extensions.nowPlus
import de.coldtea.smplr.alarm.lockscreenalarm.ActivityLockScreenAlarm
import de.coldtea.smplr.smplralarm.smplrAlarmCancel
import de.coldtea.smplr.smplralarm.smplrAlarmSet
import timber.log.Timber
import java.util.*

class AlarmViewModel: ViewModel() {

    private val cal = Calendar.getInstance()
    private val now = cal.getTimeFormattedString("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")

    fun setAlarm(hour:Int, minute: Int, weekInfo: WeekInfo, applicationContext: Context){
        val intent = Intent(
            applicationContext,
            ActivityLockScreenAlarm::class.java
        )

        intent.putExtra("SmplrText", "You did it, you crazy bastard you did it!")

        smplrAlarmSet(applicationContext) {
            hour { hour }
            min { minute }
            fullScreenIntent {
                intent
            }
            onAlarmRings{
                    alarmId -> Timber.i("SmplrAlarmApp.MainFragment.onAlarmRings: $alarmId")
            }
            weekdays {
                if( weekInfo.monday ) monday()
                if( weekInfo.tuesday ) tuesday()
                if( weekInfo.wednesday ) wednesday()
                if( weekInfo.thursday ) thursday()
                if( weekInfo.friday ) friday()
                if( weekInfo.saturday ) saturday()
                if( weekInfo.sunday ) sunday()
            }
        }
    }

    fun setAlarmIn(minutes: Int, applicationContext: Context): AlarmInfo{
        val intent = Intent(
            applicationContext,
            ActivityLockScreenAlarm::class.java
        )

        val hourMin = cal.nowPlus(minutes)

        intent.putExtra("SmplrText", "You did it, you crazy bastard you did it!")

        val requestCode = createBasicNotificationWithFullScreenIntent(hourMin, intent, applicationContext)

        return AlarmInfo(requestCode, hourMin)
    }

    private fun createBasicNotificationWithFullScreenIntent(timePair: Pair<Int,Int>, intent: Intent, applicationContext: Context) = smplrAlarmSet(applicationContext) {
        hour { timePair.first }
        min { timePair.second }
        fullScreenIntent {
            intent
        }
        onAlarmRings{
                alarmId -> Timber.i("SmplrAlarmApp.MainFragment.onAlarmRings: $alarmId")
        }
    }

    fun cancelAlarm(requestCode: Int, applicationContext: Context) = smplrAlarmCancel(applicationContext){
        requestCode { requestCode }
    }

}