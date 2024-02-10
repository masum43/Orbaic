package com.orbaic.miner.homeNew

sealed class CountdownState {
    object Idle : CountdownState()
    data class Running(val timeRemaining: String) : CountdownState()
    data class Error(var errorStatus: Int, val errorMessage: String) : CountdownState()
    object Finished : CountdownState()
}