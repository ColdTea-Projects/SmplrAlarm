package de.coldtea.smplr.alarm.alarms

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.coldtea.smplr.alarm.MainActivity
import de.coldtea.smplr.alarm.alarms.models.WeekInfo
import de.coldtea.smplr.alarm.lockscreenalarm.ActivityLockScreenAlarm
import de.coldtea.smplr.smplralarm.*
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmListRequestAPI
import timber.log.Timber

class AlarmViewModel : ViewModel() {

    lateinit var smplrAlarmListRequestAPI: SmplrAlarmListRequestAPI

    private val _alarmListAsJson = MutableLiveData<String>()
    val alarmListAsJson: LiveData<String>
        get() = _alarmListAsJson

    fun initAlarmListListener(applicationContext: Context) =
        smplrAlarmChangeOrRequestListener(applicationContext) {
            _alarmListAsJson.postValue(it)
        }.also {
            smplrAlarmListRequestAPI = it
        }

    fun setFullScreenIntentAlarm(
        hour: Int,
        minute: Int,
        weekInfo: WeekInfo,
        applicationContext: Context
    ): Int {
        val onClickShortcutIntent = Intent(
            applicationContext,
            MainActivity::class.java
        )

        val fullScreenIntent = Intent(
            applicationContext,
            ActivityLockScreenAlarm::class.java
        )

        fullScreenIntent.putExtra("SmplrText", "You did it, you crazy bastard you did it!")

        return smplrAlarmSet(applicationContext) {
            hour { hour }
            min { minute }
            intent { onClickShortcutIntent }
            receiverIntent { fullScreenIntent }
            onAlarmRings { alarmId ->
                Timber.i("SmplrAlarmApp.MainFragment.onAlarmRings: $alarmId")
            }
            weekdays {
                if (weekInfo.monday) monday()
                if (weekInfo.tuesday) tuesday()
                if (weekInfo.wednesday) wednesday()
                if (weekInfo.thursday) thursday()
                if (weekInfo.friday) friday()
                if (weekInfo.saturday) saturday()
                if (weekInfo.sunday) sunday()
            }
        }
    }

    fun setNotificationAlarm(
        hour: Int,
        minute: Int,
        weekInfo: WeekInfo,
        applicationContext: Context
    ): Int =
        smplrAlarmSet(applicationContext) {
            hour { hour }
            min { minute }
            onAlarmRings { alarmId ->
                Timber.i("SmplrAlarmApp.MainFragment.onAlarmRings: $alarmId")
            }
            weekdays {
                if (weekInfo.monday) monday()
                if (weekInfo.tuesday) tuesday()
                if (weekInfo.wednesday) wednesday()
                if (weekInfo.thursday) thursday()
                if (weekInfo.friday) friday()
                if (weekInfo.saturday) saturday()
                if (weekInfo.sunday) sunday()
            }
        }


    fun updateAlarm(
        requestCode: Int,
        hour: Int,
        minute: Int,
        weekInfo: WeekInfo,
        isActive: Boolean,
        applicationContext: Context
    ) {
        smplrAlarmUpdate(applicationContext) {
            requestCode { requestCode }
            hour { hour }
            min { minute }
            weekdays {
                if (weekInfo.monday) monday()
                if (weekInfo.tuesday) tuesday()
                if (weekInfo.wednesday) wednesday()
                if (weekInfo.thursday) thursday()
                if (weekInfo.friday) friday()
                if (weekInfo.saturday) saturday()
                if (weekInfo.sunday) sunday()
            }
            isActive { isActive }
        }
    }

    fun requestAlarmList() = smplrAlarmListRequestAPI.requestAlarmList()

    fun cancelAlarm(requestCode: Int, applicationContext: Context) =
        smplrAlarmCancel(applicationContext) {
            requestCode { requestCode }
        }

}