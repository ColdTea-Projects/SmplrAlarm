package de.coldtea.smplr.alarm.alarmlogs

import androidx.lifecycle.ViewModel
import de.coldtea.smplr.smplralarm.alarmlogs.LogsRepository

class AlarmLogsViewModel(
    val repository: LogsRepository
) : ViewModel() {

    private val logs
        get() = repository.alarms?.toMutableList()

    fun getLogsOutput() = logs?.joinToString(separator = "\n") { "Alarm rang at ${it.dateTime} / ${it.errorReport} " }

}