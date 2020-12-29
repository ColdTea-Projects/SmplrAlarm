package de.coldtea.smplr.alarm

import android.app.Application
import timber.log.Timber

class SmplrAlarmApp: Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}