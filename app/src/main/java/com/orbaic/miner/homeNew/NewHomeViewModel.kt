package com.orbaic.miner.homeNew

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class NewHomeViewModel : ViewModel() {
    private val mAuth = FirebaseAuth.getInstance()
    private val rootRef = FirebaseDatabase.getInstance().reference

    private val _userData = MutableLiveData<User?>()
    val userData: LiveData<User?> get() = _userData

    fun fetchData() {
        viewModelScope.launch {
            mAuth.currentUser?.uid?.let { userId ->
                try {
                    val snapshot = withContext(Dispatchers.IO) {
                        rootRef.child("users").child(userId).get().await()
                    }
                    val user = snapshot.getValue(User::class.java)
                    _userData.postValue(user)
                } catch (e: Exception) {
                    // Handle errors
                    _userData.postValue(null) // Post null to indicate error
                    Log.e("NewHomeViewModel", "Error fetching data: ${e.message}", e)
                }
            }
        }
    }

    private val countdownStateFlow: MutableStateFlow<CountdownState> = MutableStateFlow(CountdownState.Idle)

    fun startCountdown(miningStartTimeMillis: Long) {
        viewModelScope.launch {
            countdownStateFlow.emit(CountdownState.Running(miningStartTimeMillis.toString()))

            val currentTimeMillis = System.currentTimeMillis()
            val timeDifference = miningStartTimeMillis - currentTimeMillis

            Log.e("remainingTime", "miningStartTimeMillis: $miningStartTimeMillis")
            Log.e("remainingTime", "currentTimeMillis: $currentTimeMillis")
            Log.e("remainingTime", "timeDifference: $timeDifference")

            if (timeDifference <= 0) {
                countdownStateFlow.emit(CountdownState.Finished)
                return@launch
            }

            var remainingTimeMillis = timeDifference
            while (remainingTimeMillis > 0) {
                val delayMillis = kotlin.math.min(remainingTimeMillis, 1000L)
                delay(delayMillis)
                remainingTimeMillis -= delayMillis

                val secondsRemaining = remainingTimeMillis / 1000
                val minutes = secondsRemaining / 60
                val seconds = secondsRemaining % 60
                countdownStateFlow.emit(CountdownState.Running("$minutes:$seconds"))
            }

            countdownStateFlow.emit(CountdownState.Finished)
        }
    }

    fun getCountdownStateFlow(): Flow<CountdownState> {
        return countdownStateFlow
    }
}

