package de.coldtea.smplr.alarm

import android.app.Application
import de.coldtea.smplr.alarm.di.logsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class SmplrAlarmApp: Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        // Start Koin
        startKoin{
            androidLogger()
            androidContext(this@SmplrAlarmApp)

            modules(
                logsModule
            )
        }
    }
}