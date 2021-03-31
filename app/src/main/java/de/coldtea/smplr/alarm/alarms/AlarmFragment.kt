package de.coldtea.smplr.alarm.alarms

import android.content.Intent
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
import java.util.*

class AlarmFragment : Fragment() {

    lateinit var binding: FragmentAlarmsBinding

    private val viewModel by viewModels<AlarmViewModel>()

    var requestCodeAlarm1 = -1
    var requestCodeAlarm2 = -1

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


        binding.setAlarm1.setOnClickListener {

            val alarmInfo = viewModel.setAlarmIn(1, requireContext().applicationContext)
            Toast.makeText(
                requireContext(),
                "${alarmInfo.time.first}:${alarmInfo.time.second}",
                LENGTH_SHORT
            ).show()
            requestCodeAlarm1 = alarmInfo.requestCode

        }

        binding.setAlarm2.setOnClickListener {

            val alarmInfo = viewModel.setAlarmIn(2, requireContext().applicationContext)
            Toast.makeText(
                requireContext(),
                "${alarmInfo.time.first}:${alarmInfo.time.second}",
                LENGTH_SHORT
            ).show()
            requestCodeAlarm2 = alarmInfo.requestCode

        }

        binding.cancelAlarm1.setOnClickListener {
            if (requestCodeAlarm1 != -1) viewModel.cancelAlarm(
                requestCodeAlarm1,
                requireContext().applicationContext
            )
        }

        binding.cancelAlarm2.setOnClickListener {
            if (requestCodeAlarm2 != -1) viewModel.cancelAlarm(
                requestCodeAlarm2,
                requireContext().applicationContext
            )
        }

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

            viewModel.setAlarm(
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

        }

        binding.updateList.setOnClickListener {
            viewModel.requestAlarmList()
        }
    }
}