package de.coldtea.smplr.alarm.alarmlogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.coldtea.smplr.alarm.R
import de.coldtea.smplr.alarm.databinding.FragmentAlarmLogsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlarmLogsFragment: Fragment() {
    lateinit var binding: FragmentAlarmLogsBinding

    private val viewModel by viewModel<AlarmLogsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAlarmLogsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logs.text = viewModel.getLogsOutput()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actions_log -> {
                binding.logs.text = viewModel.getLogsOutput()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}