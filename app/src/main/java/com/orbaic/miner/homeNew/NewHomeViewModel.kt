package com.orbaic.miner.homeNew

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.orbaic.miner.ReferralDataRecive
import com.orbaic.miner.common.Config
import com.orbaic.miner.common.Constants
import com.orbaic.miner.common.ResponseState
import com.orbaic.miner.common.SpManager
import com.orbaic.miner.common.checkMiningStatusTeam
import com.orbaic.miner.common.getRewardTokenFromJson
import com.orbaic.miner.home.MyRewardedTokenItem
import com.orbaic.miner.myTeam.Team
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

class NewHomeViewModel : ViewModel() {
    private var mAuth = FirebaseAuth.getInstance()
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

    val userDataFlow: Flow<ResponseState<User?>> = flow {
        try {
            emit(ResponseState.Loading)
            val userId = mAuth.currentUser?.uid
            if (userId != null) {
                val snapshot = withContext(Dispatchers.IO) {
                    rootRef.child("users").child(userId).get().await()
                }
                val user = snapshot.getValue(User::class.java)
                emit(ResponseState.Success(user))
            } else {
                emit(ResponseState.Error("User not authenticated"))
            }
        } catch (e: Exception) {
            Log.e("userDataFlow", "Error: "+e.localizedMessage)
            emit(ResponseState.Error(e.localizedMessage))
        }
    }.flowOn(Dispatchers.IO)


    fun checkEmailVerifyStatus(isEmailNotVerified: () -> Unit) {
        mAuth.currentUser?.reload()?.addOnSuccessListener {
            println(mAuth.currentUser?.isEmailVerified)
            if (!mAuth.currentUser?.isEmailVerified!!) {
                isEmailNotVerified.invoke()
            }
        }
    }

    fun sendEmailVerification() {
        mAuth.currentUser?.sendEmailVerification()
    }

    fun giveUserMiningReferQuizPoint(onSuccess: () -> Unit,
                                     onFailure: () -> Unit) {
        viewModelScope.launch {
            mAuth.currentUser?.uid?.let { userId ->
                try {
                    val snapshot = withContext(Dispatchers.IO) {
                        rootRef.child("users").child(userId).get().await()
                    }
                    val user = snapshot.getValue(User::class.java)

                    if (user != null) {
                        val miningEarnedCoin = SpManager.getDouble(SpManager.KEY_POINTS_EARNED, 0.0)
                        val referEarnedPoints = SpManager.getDouble(SpManager.KEY_POINTS_REFER_EARNED, 0.0)
                        val correctQuizAnsCoin = SpManager.getInt(SpManager.KEY_CORRECT_ANS, 0)
                        val quizCountEarned = SpManager.getInt(SpManager.KEY_QUIZ_COUNT, 0)


                        val currentPoints = when (val userPoint = user.point) {
                            is String -> userPoint.toDoubleOrNull() ?: 0.0
                            is Number -> userPoint.toDouble()
                            else -> 0.0
                        }

                        val newMiningQuizPoints = currentPoints + miningEarnedCoin + correctQuizAnsCoin

                        val currentReferralPoints = when (val userReferralPoint = user.referralPoint) {
                            is String -> userReferralPoint.toDoubleOrNull() ?: 0.0
                            is Number -> userReferralPoint.toDouble()
                            else -> 0.0
                        }
                        val newReferralPoints = currentReferralPoints + referEarnedPoints

                        val updatedQuizCount = if (user.qz_count.isNotEmpty()) {
                            user.qz_count.toInt() + quizCountEarned
                        } else quizCountEarned

                        val updatedMiningCount = if (user.mining_count.isNotEmpty()) {
                            user.qz_count.toInt() + Config.miningCountReward
                        } else Config.miningCountReward


                        // Update the user's points atomically
                        val hashMap: HashMap<String, Any> = HashMap()
                        hashMap["point"] = newMiningQuizPoints.toString()
                        hashMap["referralPoint"] = newReferralPoints.toString()
                        hashMap["extra3"] = Constants.STATE_MINING_FINISHED.toString()
                        hashMap["qz_count"] = updatedQuizCount.toString()
                        hashMap["mining_count"] = updatedMiningCount.toString()
                        rootRef.child("users").child(userId).updateChildren(hashMap)
                            .addOnSuccessListener {
                                Log.d("giveUserPoint", "User points updated successfully")
                                onSuccess.invoke()
                            }
                            .addOnFailureListener { e ->
                                Log.e("giveUserPoint", "Failed to update user points: ${e.message}", e)
                                onFailure.invoke()
                            }
                    } else {
                        Log.e("giveUserPoint", "User data is null")
                        onFailure.invoke()
                    }
                } catch (e: Exception) {
                    Log.e("giveUserPoint", "Error fetching user data: ${e.message}", e)
                    onFailure.invoke()
                }
            }
        }
    }




    private val countdownStateFlow: MutableStateFlow<CountdownState> = MutableStateFlow(CountdownState.Idle)
    private var countdownJob: Job? = null
    fun startMiningCountdown(miningStartTimeMillis: Long) {
        viewModelScope.launch {
            countdownStateFlow.emit(CountdownState.Idle)
            val miningEndTimeMillis = miningStartTimeMillis + (24 * 60 * 60 * 1000)
            val currentTimeMillis = System.currentTimeMillis()
            val timeDifference = miningEndTimeMillis - currentTimeMillis

            Log.e("remainingTime", "miningStartTimeMillis: $miningStartTimeMillis")
            Log.e("remainingTime", "miningEndTimeMillis: $miningEndTimeMillis")
            Log.e("remainingTime", "currentTimeMillis: $currentTimeMillis")
            Log.e("remainingTime", "timeDifference: $timeDifference")

            if (timeDifference <= 0) {
                countdownStateFlow.emit(CountdownState.Finished)
                return@launch
            }

            countdownJob?.cancel()
            countdownJob = viewModelScope.launch {
                var remainingTimeMillis = timeDifference
                while (remainingTimeMillis > 0) {
                    val delayMillis = kotlin.math.min(remainingTimeMillis, 1000L)
                    delay(delayMillis)
                    remainingTimeMillis -= delayMillis

                    val totalSecondsRemaining = remainingTimeMillis / 1000
                    val hours = totalSecondsRemaining / 3600
                    val minutes = (totalSecondsRemaining % 3600) / 60
                    val seconds = totalSecondsRemaining % 60

                    val formattedTime = String.format(Locale.ENGLISH,"%02d:%02d:%02d", hours, minutes, seconds)
                    countdownStateFlow.emit(CountdownState.Running(formattedTime))
                }

                countdownStateFlow.emit(CountdownState.Finished)
            }

        }
    }
    fun getMiningCountdownStateFlow(): Flow<CountdownState> {
        return countdownStateFlow
    }

    fun stopMiningCountdown() {
        countdownJob?.cancel()
        countdownStateFlow.value = CountdownState.Idle // Reset the countdown state to Idle
    }


    private val quizCountdownStateFlow: MutableStateFlow<CountdownState> = MutableStateFlow(CountdownState.Idle)
    private var quizCountdownJob: Job? = null
    fun startQuizCountdown(miningEndTimeMillis: Long, plusHours: Int) {
        viewModelScope.launch {
            quizCountdownStateFlow.emit(CountdownState.Idle)

            val currentTimeMillis = System.currentTimeMillis()
            val endTimeMillis = miningEndTimeMillis + plusHours * 3600 * 1000 // Convert hours to milliseconds

            val timeDifference = endTimeMillis - currentTimeMillis

            Log.e("startQuizCountdown", "quizEndTimeMillis: $miningEndTimeMillis")
            Log.e("startQuizCountdown", "currentTimeMillis: $currentTimeMillis")
            Log.e("startQuizCountdown", "timeDifference: $timeDifference")

            if (timeDifference <= 0) {
                quizCountdownStateFlow.emit(CountdownState.Finished)
                return@launch
            }

            quizCountdownJob?.cancel()
            quizCountdownJob = viewModelScope.launch {
                var remainingTimeMillis = timeDifference
                while (remainingTimeMillis > 0) {
                    val delayMillis = kotlin.math.min(remainingTimeMillis, 1000L)
                    delay(delayMillis)
                    remainingTimeMillis -= delayMillis

                    val totalSecondsRemaining = remainingTimeMillis / 1000
                    val hours = totalSecondsRemaining / 3600
                    val minutes = (totalSecondsRemaining % 3600) / 60
                    val seconds = totalSecondsRemaining % 60

                    val formattedTime = String.format(Locale.ENGLISH,"%02d:%02d:%02d", hours, minutes, seconds)
                    Log.e("startQuizCountdown", "formattedTime: $formattedTime")
                    quizCountdownStateFlow.emit(CountdownState.Running(formattedTime))
                }

                quizCountdownStateFlow.emit(CountdownState.Finished)
            }

        }
    }
    fun getQuizCountdownStateFlow(): Flow<CountdownState> {
        return quizCountdownStateFlow
    }
    fun stopQuizCountdown() {
        quizCountdownJob?.cancel()
        quizCountdownStateFlow.value = CountdownState.Idle // Reset the countdown state to Idle
    }



    private val _teamListLiveData = MutableLiveData<List<Team>>()
    val teamListLiveData: LiveData<List<Team>> = _teamListLiveData


    val myTeamMinerList = mutableListOf<Team>()

    fun getMyTeam() {
        viewModelScope.launch {
            try {
                val teamList = mutableListOf<Team>()
                val ref = FirebaseDatabase.getInstance().getReference("referralUser")
                    .child(mAuth.uid ?: "")
                ref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        teamList.clear()
                        myTeamMinerList.clear()
                        for (dataSnapshot in snapshot.children) {
                            val data = dataSnapshot.getValue(ReferralDataRecive::class.java)
                            val miningStatus = data?.status?.checkMiningStatusTeam()
                            teamList.add(Team(dataSnapshot.key ?: "", data?.name ?: "", "", "",
                                data?.status ?: "", miningStatus))
                        }

                        teamList.sortBy { it.userName }

                        for (miningData in teamList) {
                            if (miningData.miningStatus == Constants.STATUS_ON) {
                                myTeamMinerList.add(miningData)
                            }
                        }

                        _teamListLiveData.value = teamList
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println("$error")
                    }
                })
            } catch (e: Exception) {
                println("Error: $e")
            }
        }
    }



    fun claimReward(
        rewardedTokenCode: String,
        bonusToken: Long,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val myRewardedTokensRef = mAuth.currentUser?.uid?.let { rootRef.child("my_rewarded_tokens").child(it) }
        myRewardedTokensRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var tokenFound = false
                if (dataSnapshot.exists()) {
                    for (mSnap in dataSnapshot.children) {
                        val rewardedTokenItem = mSnap.getValue(MyRewardedTokenItem::class.java)
                        if (rewardedTokenItem!!.code == rewardedTokenCode) {
                            tokenFound = true
                            val balance = rewardedTokenItem.balance.toLong()
                            val updatedBalance = balance + bonusToken
                            myRewardedTokensRef.child(rewardedTokenItem.id.toString())
                                .child("balance").setValue(updatedBalance.toString())
                                .addOnSuccessListener {
                                    onSuccess.invoke()
                                }
                                .addOnFailureListener { e ->
                                    onFailure.invoke()
                                }
                            break
                        }
                    }
                }

                if (!tokenFound) {
                    val rewardedTokenItemFromJson = getRewardTokenFromJson(rewardedTokenCode)
                    rewardedTokenItemFromJson.balance = bonusToken.toString()
                    myRewardedTokensRef.push()
                        .setValue(rewardedTokenItemFromJson)
                        .addOnSuccessListener {
                            onSuccess.invoke()
                        }
                        .addOnFailureListener {
                            onFailure.invoke()
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure.invoke()
            }
        })
    }


    fun addMiningCount(count: String?) {
        val myRef: DatabaseReference? = mAuth.currentUser?.uid?.let { rootRef.child("users").child(it) }
        myRef?.child("mining_count")?.setValue(count)
    }

    fun addQuizCount(qzCountStr: String) {
        val hashMap = HashMap<String, Any>()
        hashMap["qz_count"] = qzCountStr
        val myRef: DatabaseReference? = mAuth.currentUser?.uid?.let { rootRef.child("users").child(it) }
        myRef?.updateChildren(hashMap)
    }



}

