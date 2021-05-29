package de.coldtea.smplr.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

class ActionReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Timber.i(intent.action.toString())
    }

}