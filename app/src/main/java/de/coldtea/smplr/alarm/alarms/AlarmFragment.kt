package de.coldtea.smplr.alarm.alarms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import de.coldtea.smplr.alarm.alarms.models.WeekInfo
import de.coldtea.smplr.alarm.databinding.FragmentAlarmsBinding
import de.coldtea.smplr.alarm.extensions.nowPlus
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmAPI
import de.coldtea.smplr.smplralarm.smplrAlarmRenewMissingAlarms
import java.util.*

class AlarmFragment : Fragment() {

    lateinit var binding: FragmentAlarmsBinding

    private val viewModel by viewModels<AlarmViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAlarmsBinding.inflate(inflater, container, false)

        viewModel.initAlarmListListener(requireContext().applicationContext)
        viewModel.alarmListAsJson.observe(viewLifecycleOwner) {
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

        binding.setIntnentAlarm.setOnClickListener {

            val weekInfo = binding.getWeekInfo()

            val alarmId = viewModel.setFullScreenIntentAlarm(
                binding.hour.text.toString().toInt(),
                binding.minute.text.toString().toInt(),
                weekInfo,
                requireContext().applicationContext
            )

            binding.toastAlarm()
            binding.alarmId.setText(alarmId.toString())
        }

        binding.setNotificationAlarm.setOnClickListener {

            val weekInfo = binding.getWeekInfo()

            val alarmId = viewModel.setNotificationAlarm(
                binding.hour.text.toString().toInt(),
                binding.minute.text.toString().toInt(),
                weekInfo,
                requireContext().applicationContext
            )

            binding.toastAlarm()
            binding.alarmId.setText(alarmId.toString())
        }


        binding.updateList.setOnClickListener {
            viewModel.requestAlarmList()
        }

        binding.updateAlarms.setOnClickListener {
            val weekInfo = binding.getWeekInfo()

            viewModel.updateAlarm(
                binding.alarmId.text.toString().toInt(),
                binding.hour.text.toString().toInt(),
                binding.minute.text.toString().toInt(),
                weekInfo,
                binding.isActive.isChecked,
                requireContext().applicationContext
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

        binding.checkAlarm.setOnClickListener {
            val intent = SmplrAlarmAPI.getAlarmIntent(
                binding.alarmId.text.toString().toInt(),
                requireContext().applicationContext
            )

            Toast.makeText(requireContext(), intent?.toString().orEmpty(), Toast.LENGTH_LONG).show()
        }

        smplrAlarmRenewMissingAlarms(requireContext())
    }

    private fun FragmentAlarmsBinding.getWeekInfo(): WeekInfo =
        WeekInfo(
            monday.isChecked,
            tuesday.isChecked,
            wednesday.isChecked,
            thursday.isChecked,
            friday.isChecked,
            saturday.isChecked,
            sunday.isChecked
        )

    private fun FragmentAlarmsBinding.toastAlarm() {
        var toastText = "${hour.text}:${minute.text}"
        if (monday.isChecked) toastText = toastText.plus(" Monday")
        if (tuesday.isChecked) toastText = toastText.plus(" Tuesday")
        if (wednesday.isChecked) toastText = toastText.plus(" Wednesday")
        if (thursday.isChecked) toastText = toastText.plus(" Thursday")
        if (friday.isChecked) toastText = toastText.plus(" Friday")
        if (saturday.isChecked) toastText = toastText.plus(" Saturday")
        if (sunday.isChecked) toastText = toastText.plus(" Sunday")

        Toast.makeText(requireContext(), toastText, LENGTH_SHORT).show()

    }
}