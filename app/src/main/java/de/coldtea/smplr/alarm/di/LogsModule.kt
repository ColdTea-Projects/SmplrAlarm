package de.coldtea.smplr.alarm.di

import de.coldtea.smplr.alarm.alarmlogs.AlarmLogsViewModel
import de.coldtea.smplr.smplralarm.alarmlogs.LogsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val logsModule = module{

    factory { LogsRepository(androidContext().applicationContext) }
    viewModel { AlarmLogsViewModel(get()) }

}