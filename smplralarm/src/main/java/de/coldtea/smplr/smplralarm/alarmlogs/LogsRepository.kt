package de.coldtea.smplr.smplralarm.alarmlogs

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class LogsRepository(private val context: Context) {
    private val sharedPreferences
        by lazy { context.getSharedPreferences(KEY_SMPLR_ALARM, Context.MODE_PRIVATE) }

    val alarms: List<RangAlarmObject>?
        get() {
            val list = Types.newParameterizedType(List::class.java, RangAlarmObject::class.java)
            val adapter: JsonAdapter<List<RangAlarmObject>> = Moshi.Builder().build().adapter(list)

            sharedPreferences.getString(KEY_ALARMS_LOG, "").let {
                return if(it.isNullOrBlank()) listOf() else adapter.fromJson(it)
            }
        }

    fun logAlarm(alarm: RangAlarmObject) = with(sharedPreferences.edit()){
        val alarmList = alarms?.toMutableList()
        alarmList?.add(alarm)
        val list = Types.newParameterizedType(List::class.java, RangAlarmObject::class.java)
        val adapter: JsonAdapter<List<RangAlarmObject>> = Moshi.Builder().build().adapter(list)

        val jsonString = adapter.toJson(alarmList)
        putString(KEY_ALARMS_LOG, jsonString)
        commit()
    }

    companion object{
        const val KEY_SMPLR_ALARM = "key_smplr_alarm"
        const val KEY_ALARMS_LOG = "key_alarms_log"
    }
}