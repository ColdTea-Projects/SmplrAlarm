package de.coldtea.smplr.smplralarm.apis

import android.content.Context
import de.coldtea.smplr.smplralarm.extensions.alarmsAsJsonString
import de.coldtea.smplr.smplralarm.models.ActiveAlarmList
import de.coldtea.smplr.smplralarm.models.AlarmItem
import de.coldtea.smplr.smplralarm.repository.AlarmNotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by [Yasar Naci Gündüz](https://github.com/ColdTea-Projects).
 */
class SmplrAlarmListRequestAPI(val context: Context) {

    internal var alarmListChangeOrRequestedListener: ((String) -> Unit)? = null

    private var alarmListJson: String = ""
        set(value) {
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
                    it.isActive,
                    it.infoPairs
                )
            }

            alarmListJson = ActiveAlarmList(alarmList).alarmsAsJsonString().orEmpty()
        }
    }


}