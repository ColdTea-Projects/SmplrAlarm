package de.coldtea.smplr.alarm

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.coldtea.smplr.alarm.databinding.FragmentMainBinding
import de.coldtea.smplr.alarm.lockscreenalarm.ActivityLockScreenAlarm
import de.coldtea.smplr.smplralarm.smplrAlarmSet
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainFragment : Fragment() {

    lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cal = Calendar.getInstance()

        binding.setAlarm1.setOnClickListener {
            createBasicNotificationWithFullScreenIntent(cal, 1)
        }

        binding.setAlarm2.setOnClickListener {
            createBasicNotificationWithFullScreenIntent(cal, 2)
        }

        binding.setAlarm3.setOnClickListener {
            createBasicNotificationWithFullScreenIntent(cal, 3)
        }

        binding.setAlarm4.setOnClickListener {
            createBasicNotificationWithFullScreenIntent(cal, 4)
        }

    }

    private fun createBasicNotificationWithFullScreenIntent(cal: Calendar, delay: Int) = smplrAlarmSet(requireContext().applicationContext) {
        hour { cal.get(Calendar.HOUR_OF_DAY) }
        min { cal.get(Calendar.MINUTE) + delay }
        fullScreenIntent {
            Intent(
                requireContext().applicationContext,
                ActivityLockScreenAlarm::class.java
            )
        }
    }

}