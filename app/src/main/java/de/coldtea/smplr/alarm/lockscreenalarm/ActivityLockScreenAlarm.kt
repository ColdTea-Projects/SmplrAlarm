package de.coldtea.smplr.alarm.lockscreenalarm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.coldtea.smplr.alarm.databinding.ActivityLockScreenAlarmBinding
import de.coldtea.smplr.alarm.extensions.activateLockScreen
import de.coldtea.smplr.alarm.extensions.deactivateLockScreen

class ActivityLockScreenAlarm : AppCompatActivity(){

    private lateinit var binding: ActivityLockScreenAlarmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockScreenAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val a = intent.getStringExtra("SmplrText")
        binding.textView.append(a)

        activateLockScreen()
    }

    override fun onDestroy() {
        super.onDestroy()
        deactivateLockScreen()
    }
}