package de.coldtea.smplr.smplralarm.managers

import android.content.Context
import de.coldtea.smplr.smplralarm.extensions.alarmsAsJsonString
import de.coldtea.smplr.smplralarm.models.ActiveAlarmList
import de.coldtea.smplr.smplralarm.models.AlarmItem
import de.coldtea.smplr.smplralarm.repository.AlarmNotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmListRequestManager(val context: Context) {

    internal var alarmListChangeOrRequestedListener: ((String) -> Unit)? = null

    private var alarmListJson: String = ""
        set(value) {

            if (value == field) return
            field = value

            alarmListChangeOrRequestedListener?.invoke(value)

        }

    private val alarmNotificationRepository: AlarmNotificationRepository by lazy {
        AlarmNotificationRepository(context)
    }

    fun requestAlarmList() {
        CoroutineScope(Dispatchers.IO).launch {
            val alarmList = alarmNotificationRepository.getAllAlarmNotifications().map {
                AlarmItem(
                    it.alarmNotificationId,
                    it.hour,
                    it.min,
                    it.weekDays,
                    it.isActive
                )
            }

            alarmListJson = ActiveAlarmList(alarmList).alarmsAsJsonString().orEmpty()
        }
    }


}