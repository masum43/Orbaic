package com.orbaic.miner.homeNew

sealed class CountdownState {
    object Idle : CountdownState()
    data class Running(val timeRemaining: String) : CountdownState()
    object Finished : CountdownState()
}