package de.coldtea.smplr.alarm.alarms

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import de.coldtea.smplr.alarm.alarms.models.WeekInfo
import de.coldtea.smplr.alarm.databinding.FragmentAlarmsBinding
import de.coldtea.smplr.alarm.extensions.nowPlus
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmAPI
import de.coldtea.smplr.smplralarm.smplrAlarmRenewMissingAlarms
import java.util.*

class AlarmFragment : Fragment() {

    lateinit var binding: FragmentAlarmsBinding

    private val viewModel by viewModels<AlarmViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAlarmsBinding.inflate(inflater, container, false)

        viewModel.initAlarmListListener(requireContext().applicationContext)
        viewModel.alarmListAsJson.observe(viewLifecycleOwner){
            binding.alarmListJson.text = it
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val defaultTime = Calendar.getInstance().nowPlus(1)

        binding.hour.setText(defaultTime.first.toString())
        binding.minute.setText(defaultTime.second.toString())
        binding.sunday.isChecked = true

        binding.setRepeatingAlarm.setOnClickListener {

            val weekInfo = WeekInfo(
                binding.monday.isChecked,
                binding.tuesday.isChecked,
                binding.wednesday.isChecked,
                binding.thursday.isChecked,
                binding.friday.isChecked,
                binding.saturday.isChecked,
                binding.sunday.isChecked
            )

            val alarmId = viewModel.setAlarm(
                binding.hour.text.toString().toInt(),
                binding.minute.text.toString().toInt(),
                weekInfo,
                requireContext().applicationContext
            )

            var toastText = "${binding.hour.text}:${binding.minute.text}"
            if (binding.monday.isChecked) toastText = toastText.plus(" Monday")
            if (binding.tuesday.isChecked) toastText = toastText.plus(" Tuesday")
            if (binding.wednesday.isChecked) toastText = toastText.plus(" Wednesday")
            if (binding.thursday.isChecked) toastText = toastText.plus(" Thursday")
            if (binding.friday.isChecked) toastText = toastText.plus(" Friday")
            if (binding.saturday.isChecked) toastText = toastText.plus(" Saturday")
            if (binding.sunday.isChecked) toastText = toastText.plus(" Sunday")

            Toast.makeText(requireContext(), toastText, LENGTH_SHORT).show()

            binding.alarmId.setText(alarmId.toString())
        }

        binding.updateList.setOnClickListener {
            viewModel.requestAlarmList()
        }

        binding.updateAlarms.setOnClickListener {
            val weekInfo = WeekInfo(
                binding.monday.isChecked,
                binding.tuesday.isChecked,
                binding.wednesday.isChecked,
                binding.thursday.isChecked,
                binding.friday.isChecked,
                binding.saturday.isChecked,
                binding.sunday.isChecked
            )

            viewModel.updateAlarm(
                binding.alarmId.text.toString().toInt(),
                binding.hour.text.toString().toInt(),
                binding.minute.text.toString().toInt(),
                weekInfo,
                binding.isActive.isChecked,
                requireContext().applicationContext
            )
        }

        binding.cancelAlarm.setOnClickListener {
            viewModel.cancelAlarm(
                binding.alarmId.text.toString().toInt(),
                requireContext().applicationContext
            )
        }
    }
}