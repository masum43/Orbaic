package com.orbaic.miner.homeNew

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.orbaic.miner.R
import com.orbaic.miner.common.roundTo
import com.orbaic.miner.common.toast
import com.orbaic.miner.databinding.FragmentNewHomeBinding
import com.wada811.viewbinding.viewBinding
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class NewHomeFragment : Fragment(R.layout.fragment_new_home) {
    private val binding by viewBinding(FragmentNewHomeBinding::bind)
    private val viewModel: NewHomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchData()
        observeCountdownState()
    }

    private fun fetchData() {
        viewModel.fetchData()
        viewModel.userData.observe(viewLifecycleOwner) { user ->
            Log.e("user123", "onDataChange: ${user?.name}")
            binding.aciCoin.text = user?.point?.roundTo()

            lifecycleScope.launch {
                val timeStatus = user?.isWithin24Hours()
                handleTimeStatus(timeStatus, user?.miningStartTime.toString())
            }
        }
    }

    private fun handleTimeStatus(timeStatus: TimeStatus?, miningStartTime: String) {
        when (timeStatus?.status) {
            1 -> { // Mining start time is within 24 hours
                viewModel.startCountdown(miningStartTime.toLong())
            }
            2 -> {
                val errorMessage = timeStatus.message ?: "Unknown error"
                errorMessage.toast()
            }
            else -> {
                val errorMessage = timeStatus?.message ?: "Unknown error"
                errorMessage.toast()
            }
        }
    }

    private fun observeCountdownState() {
        lifecycleScope.launch {
            viewModel.getCountdownStateFlow().collect { state ->
                when (state) {
                    is CountdownState.Running -> {
                        Log.e("remainingTime", "state: Running")
                        val remainingTime = state.timeRemaining
                        binding.hourFragment.text = remainingTime
                        Log.e("remainingTime", "observeCountdownState: $remainingTime")
                    }
                    is CountdownState.Finished -> {
                        Log.e("remainingTime", "state: Finished")
                    }
                    else -> {
                        // Handle other states if needed
                        Log.e("remainingTime", "state: else")
                    }
                }
            }
        }
    }
}