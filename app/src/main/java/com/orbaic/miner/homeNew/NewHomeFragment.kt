package com.orbaic.miner.homeNew

import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.orbaic.miner.AdMobAds
import com.orbaic.miner.BuildConfig
import com.orbaic.miner.MainActivity2
import com.orbaic.miner.MyApp
import com.orbaic.miner.R
import com.orbaic.miner.TeamMembersFragment
import com.orbaic.miner.allNews.AllNewsFragment
import com.orbaic.miner.auth.LoginLayout
import com.orbaic.miner.common.Config
import com.orbaic.miner.common.Constants
import com.orbaic.miner.common.ErrorDialog
import com.orbaic.miner.common.ProgressDialog
import com.orbaic.miner.common.ResponseState
import com.orbaic.miner.common.RetrofitClient2
import com.orbaic.miner.common.SpManager
import com.orbaic.miner.common.gone
import com.orbaic.miner.common.invisible
import com.orbaic.miner.common.parseNumber
import com.orbaic.miner.common.roundTo
import com.orbaic.miner.common.show
import com.orbaic.miner.common.toast
import com.orbaic.miner.databinding.FragmentNewHomeBinding
import com.orbaic.miner.home.Post2.Post2Item
import com.orbaic.miner.myTeam.MyTeamAdapter
import com.orbaic.miner.quiz.QuizStartActivity
import com.orbaic.miner.wordpress.PostAdapter2
import com.unity3d.services.core.properties.ClientProperties
import com.vungle.ads.internal.util.ThreadUtil.runOnUiThread
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone


class NewHomeFragment : Fragment() {
    private lateinit var binding : FragmentNewHomeBinding
    private val viewModel: NewHomeViewModel by viewModels()
    private val adapterTeam by lazy { MyTeamAdapter() }
    private val progressDialog by lazy { ProgressDialog.Builder(requireContext()).build() }
    //private val errorDialog by lazy { ErrorDialog(requireActivity()) }
    //private val mobAds by lazy { AdMobAds(requireContext(), requireActivity()) }
    private lateinit var errorDialog: ErrorDialog
    private lateinit var mobAds: AdMobAds
    private var isDrawerProfileUpdated = false

    private val dataFetchActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            fetchData()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                fetchData()
            } else {
                showLocationWarning()
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNewHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SpManager.init(requireContext())
        errorDialog = ErrorDialog(requireActivity())
        mobAds = AdMobAds(requireContext(), requireActivity())
        if (!mobAds.isAdsLoaded) mobAds.loadIntersAndRewardedAd()
        viewModel.updateTokenInDatabaseIfNeed()
        prepareRv()
        initClicks()
        observeCountdownState()
        checkEmailVerificationStatus()

//        findOldUsers()
    }

/*    private fun findOldUsers() {
        var counter = 0
        val rootRef = FirebaseDatabase.getInstance().reference
        rootRef.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    for (mSnap in snapshot.children) {
//                        val user = mSnap.getValue(User::class.java)
                        val userId = mSnap.child("id").value.toString();
                        if (userId.length < 10) {
                            Log.e("OLD_USERS", "key: ${mSnap.key}, id: $userId")
                            counter++
                        }
                    }
                    Log.e("OLD_USERS", "total old users: $counter")
                } catch (e: Exception) {
                    Log.e("OLD_USERS", "Error processing data: ${e.message}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("OLD_USERS", "Database operation cancelled: ${error.message}")
            }
        })
    }*/


    private fun checkEmailVerificationStatus() {
        viewModel.checkEmailVerifyStatus {
            dialogShow(
                "Email verification",
                "Your email is not verified. Please check your email and verify the mail."
            )
        }
    }

    override fun onResume() {
        super.onResume()
        fetchData()

    }

    private fun prepareRv() {
        binding.rvMyTeam.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvMyTeam.adapter = adapterTeam

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
    }



    private fun fetchData() {
        Log.e("fetchData2314", "fetchData: calling")
        progressDialog.show()
        lifecycleScope.launch {
            viewModel.userDataFlow.collect { responseState ->
                when (responseState) {
                    is ResponseState.Loading -> {
                        // Handle loading state
                    }
                    is ResponseState.Success -> {
                        val user = responseState.data
                        val point = when (val userPoint = user?.point) {
                            is String -> userPoint.toDoubleOrNull() ?: 0.0
                            is Number -> userPoint.toDouble()
                            else -> 0.0
                        }

                        val referralPoint = when (val userReferralPoint = user?.referralPoint) {
                            is String -> userReferralPoint.toDoubleOrNull() ?: 0.0
                            is Number -> userReferralPoint.toDouble()
                            else -> 0.0
                        }

                        val totalPoints = (point + referralPoint).roundTo()
                        SpManager.saveString(SpManager.KEY_POINTS_FROM_SERVER, totalPoints)
//                        viewModel.setPoint(totalPoints.toDouble())
//                        binding.aciCoin.tag = totalPoints
                        val finalHourRate = (Config.hourRate.parseNumber() + Config.hourRate.parseNumber() * 0.10 * viewModel.myTeamMinerList.size).roundTo()
                        binding.tvRate.text = "$finalHourRate/h ACI"
                        SpManager.saveString(SpManager.KEY_MY_REFER_CODE, user?.referral)
                        SpManager.saveString(SpManager.KEY_REFERRED_BY_UID, user?.referredBy)

                        lifecycleScope.launch {
                            val timeStatus = user?.isMiningWithin24Hours()
                            Log.e("timeStatus123", "fetchData: $timeStatus")
                            SpManager.saveInt(SpManager.KEY_MINER_STATUS, timeStatus?.status ?: Constants.STATE_MINING_ERROR)
                            handleMiningTimeStatus(timeStatus, user?.miningStartTime.toString())
                        }

                        lifecycleScope.launch {
                            if (!user?.extra1.isNullOrEmpty()) {
                                val quizEndTime = user?.extra1?.toLong()
                                Log.e("quizEndTime", "quizEndTime: $quizEndTime")
                                val timeStatus = user?.isQuizWithin12Hours()
                                Log.e("quizEndTime", "timeStatus: $timeStatus")
                                handleQuizTimeStatus(timeStatus, quizEndTime!!)
                            }

                        }

                        miningRewardProgress(user?.mining_count)
                        quizRewardProgress(user?.qz_count)


                        if (!isDrawerProfileUpdated) {
                            isDrawerProfileUpdated = true
                            val mainActivity = activity as MainActivity2?
                            mainActivity?.updateHeader(user?.profile_image, user?.name, user?.email)
                        }
                    }
                    is ResponseState.Error -> {
                        val errorMessage = responseState.errorMessage
                        errorDialog.showError(errorMessage.toString() , onClick = {
                            requireActivity().finishAffinity()
                        })
                    }
                }
            }
        }


        viewModel.getMyTeam()
        viewModel.teamListLiveData.observe(viewLifecycleOwner) {
            val sortedTeamList = it.take(5)
            adapterTeam.updateData(sortedTeamList)
            val teamStatus = "${viewModel.myTeamMinerList.size}/${it.size}"
            binding.tvTeamStatus.text = teamStatus
            val finalHourRate = (Config.hourRate.parseNumber() + Config.hourRate.parseNumber() * 0.10 * viewModel.myTeamMinerList.size).roundTo()
            binding.tvRate.text = "$finalHourRate/h ACI"
        }
        newsFromWordpressBlog()
    }

    private fun initClicks() {
        binding.mining.setOnClickListener {
            startMining()
        }

        binding.learnAndEarn.setOnClickListener {
            val intent = Intent(context, QuizStartActivity::class.java)
            startActivity(intent)
        }

        binding.claimRewardLayout.setOnClickListener { v ->
            binding.claimRewardLayout.gone()
            viewModel.claimReward(Config.miningRewardedTokenCode, Config.miningRewardedBonusToken,
                onSuccess = {
                    "Claimed successfully. Check in wallet".toast()
                    binding.tvMiningHoursCount.text = " 0 / 720 hours"
                    viewModel.addMiningCount("0")
                },
                onFailure = {
                    Log.e("TAG", "Failed to update rewarded balance!")
                    // Handle the error
                    binding.claimRewardLayout.show()
                }
            )
        }

        binding.claimQuizRewardLayout.setOnClickListener {
            binding.claimQuizRewardLayout.gone()
            viewModel.claimReward(Config.quizRewardedTokenCode, Config.quizRewardedBonusToken,
                onSuccess = {
                    "Claimed successfully. Check in wallet".toast()
                    binding.tvQuizCount.text = " 0 / 300"
                    viewModel.addQuizCount("0")
                },
                onFailure = {
                    Log.e("TAG", "Failed to update rewarded balance!")
                    // Handle the error
                    binding.claimQuizRewardLayout.show()
                }
            )
        }

        binding.holderRefer.setOnClickListener { refer() }

        binding.tvTeamMore.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.container, TeamMembersFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.tvNewsMore.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.container, AllNewsFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun startMining() {
        val miningStatus = SpManager.getInt(SpManager.KEY_MINER_STATUS, Constants.STATE_MINING_FINISHED)
        if (miningStatus == Constants.STATE_MINING_FINISHED) {
            if (mobAds.isAdsLoaded) {
                if (viewModel.myTeamMinerList.isNotEmpty()) {
                    val dialog = Dialog(requireActivity())
                    dialog.setContentView(R.layout.dialog_extra_point)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    val tvNotice = dialog.findViewById<TextView>(R.id.tvNotice)
                    tvNotice.text = "Your " + viewModel.myTeamMinerList.size + " team member is mining now. So you will get extra : " + 10 * viewModel.myTeamMinerList.size + "%."
                    dialog.findViewById<View>(R.id.okButton).setOnClickListener { view: View? ->
                        dialog.dismiss()
                        mobAds.showRewardedVideo()
                        viewModel.setActiveStatus()
                    }
                    dialog.show()
                }
                else {
                    SpManager.saveInt(SpManager.KEY_MINER_STATUS, Constants.STATE_MINING_ON_GOING)
                    mobAds.showRewardedVideo()
                    viewModel.setActiveStatus()
                }
            }
            else {
                adNotLoadedWarning()
            }
        }
        else {
            miningAlreadyRunningWarning()
        }

    }

    private fun adNotLoadedWarning() {
        val dialogContext = if (!isAdded) {
            MyApp.context ?: runBlocking { delay(1000); requireContext() }
        } else {
            requireContext()
        }

        val dialog = Dialog(dialogContext)
        dialog.apply {
            setContentView(R.layout.dialog_mining_warning)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            findViewById<TextView>(R.id.tvWarning)?.text = "The blockchain is currently facing significant congestion. Please remain patient and try again now."
            findViewById<View>(R.id.okButton)?.setOnClickListener {
                dismiss()
                SpManager.saveBoolean(SpManager.KEY_IS_TAP_TARGET_SHOW, true)
                showTapTarget()
            }
            show()
        }
    }

    private fun miningAlreadyRunningWarning() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_mining_warning)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.findViewById<View>(R.id.okButton)
            .setOnClickListener { view: View? -> dialog.dismiss() }
        dialog.show()
    }





    private fun handleQuizTimeStatus(timeStatus: TimeStatus?, quizEndTime: Long) {
        when (timeStatus?.status) {
            1 -> { // Quiz start time is within 12 hours
                viewModel.startQuizCountdown(quizEndTime, 0)
                binding.quizWaitingLayout.show()
                binding.learnAvailable.gone()
            }
            2 -> { // Time difference between server and device
//                val errorMessage = timeStatus.message ?: "Unknown error"
//                errorMessage.toast()
            }
            else -> { // Quiz start time is not within 12 hours
                binding.quizWaitingLayout.gone()
                binding.learnAvailable.show()
            }
        }
    }

    private fun handleMiningTimeStatus(timeStatus: TimeStatus?, miningStartTime: String) {
        Log.e("fetchData2314", "timeStatus?.status: ${timeStatus?.status}")
        when (timeStatus?.status) {
            Constants.STATE_MINING_ON_GOING -> { // Mining start time is within 24 hours
                viewModel.startMiningCountdown(miningStartTime.toLong())
                startRippleEffect()
                errorDialog.dismissDialog()
                progressDialog.dismiss()
            }

            Constants.STATE_MINING_DATE_DIFF_SERVER -> { // Time difference between server and device
                viewModel.stopMiningCountdown()
                viewModel.stopQuizCountdown()
                stopRippleEffect()
                val errorMessage = timeStatus.message ?: "Unknown error"
                errorDialog.showError(errorMessage,  onClick = {
                    if (it == 1) {
                        requireActivity().finishAffinity()
                    }
                    else {
                        clearAppData(requireContext())
                    }

                },  "Ok, got it", "Clear Data")
                progressDialog.dismiss()

            }
            Constants.STATE_MINING_POINTS_NOT_GIVEN -> {
                binding.aciCoin.text = getTotalCoin().toString()
                givePointsAndStartNewMiningSession()
            }
            Constants.STATE_MINING_FINISHED -> {
                binding.aciCoin.text = getTotalCoin().toString()
                startNewMiningStartSession()
            }
            Constants.STATE_MINING_LOCATION_NOT_GRANTED -> {
                showLocationWarning()
            }
            else -> { // Mining error
                val dialog = Dialog(requireActivity())
                dialog.setContentView(R.layout.dialog_extra_point)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val tvNotice = dialog.findViewById<TextView>(R.id.tvNotice)
                tvNotice.text = "Network or server issue. Please try after some times or contact with support if problem not solved."
                val btn = dialog.findViewById<TextView>(R.id.okButton)
                btn.text = "Ok Got It"
                btn.setOnClickListener { view: View? ->
                    dialog.dismiss()
                    requireActivity().finishAffinity()

                }
                dialog.show()
            }
        }
    }

    private fun showLocationWarning() {
        errorDialog.showError("Location permission is required to use Orbaic effectively. Please grant location permission by clicking below Allow button then Permission > Location > Allow only while using the app. After that Click on Check Now. If you need assistance, feel free to reach out to our support team. Thank you!",
            onClick = {
                if (it == 1) {
                    getLocationPermission()
                }
                else {
                    fetchData()
                }
        }, "Allow", "Check Now")

/*        val dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.dialog_extra_point)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvNotice = dialog.findViewById<TextView>(R.id.tvNotice)
        tvNotice.text =
            "Location permission is required to use Orbaic effectively. Please grant location permission by clicking below Allow button then Permission > Location > Allow only while using the app. If you need assistance, feel free to reach out to our support team. Thank you!"
        val btn = dialog.findViewById<TextView>(R.id.okButton)
        btn.text = "Allow"
        btn.setOnClickListener { view: View? ->
            dialog.dismiss()
            getLocationPermission()

        }
        dialog.show()*/
    }

    private fun givePointsAndStartNewMiningSession() {
        viewModel.giveUserMiningReferQuizPoint(
            onSuccess = {
                clearGivenCoin()
                startNewMiningStartSession()
            },
            onFailure = {
                errorDialog.showError("Something went wrong. Please close the app and try again.", onClick = {
                    requireActivity().finishAffinity()
                })
            })
    }

    private fun startNewMiningStartSession() {
        errorDialog.dismissDialog()
        stopRippleEffect()
        progressDialog.dismiss()
        showTapTarget()
        SpManager.saveInt(SpManager.KEY_MINER_STATUS, Constants.STATE_MINING_FINISHED)
    }


    private fun observeCountdownState() {
        lifecycleScope.launch {
            viewModel.getMiningCountdownStateFlow().collect { state ->
                Log.e("remainingTime", "state1122: $state")
                when (state) {
                    is CountdownState.Running -> {
                        val remainingTime = state.timeRemaining
                        binding.hourFragment.text = remainingTime
                        calculateAndSavePoints(remainingTime)
                    }
                    is CountdownState.Finished -> {
                        Log.e("remainingTime", "state: Finished")
                        givePointsAndStartNewMiningSession()
                    }
                    else -> {
                        // Handle other states if needed
                        Log.e("remainingTime", "state: else")
//                        stopRippleEffect()
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.getQuizCountdownStateFlow().collect { state ->
                when (state) {
                    is CountdownState.Running -> {
                        Log.e("getQuizCountdownState", "state: Running")
                        val remainingTime = state.timeRemaining
                        binding.tvQuizCountDown.text = remainingTime
                        Log.e("getQuizCountdownState", "observeCountdownState: $remainingTime")
                    }
                    is CountdownState.Finished -> {
                        Log.e("getQuizCountdownState", "state: Finished")
                        binding.quizWaitingLayout.gone()
                        binding.learnAvailable.show()
                    }
                    else -> {
                        // Handle other states if needed
                        Log.e("getQuizCountdownState", "state: else")
                    }
                }
            }
        }
    }

    private fun showUpdatedAciCoin() {
        val totalTokens = getTotalCoin()
        Log.e("showUpdatedAciCoin", "totalTokens: $totalTokens")
        binding.aciCoin.text = totalTokens
    }

    /*private fun getTotalCoin(): String {
        val userPointFromServer = binding.aciCoin.tag.toString()
        val earnedPoints = SpManager.getDouble(SpManager.KEY_POINTS_EARNED, 0.0)
        val referEarnedPoints = SpManager.getDouble(SpManager.KEY_POINTS_REFER_EARNED, 0.0)
        val correctQuizAns = SpManager.getInt(SpManager.KEY_CORRECT_ANS, 0)
        return (userPointFromServer.parseNumber() + earnedPoints + referEarnedPoints + correctQuizAns).roundTo()
    }*/
    private fun getTotalCoin(): String {
//        val userPointFromServer = binding.aciCoin.tag.toString()
//        val userPointFromServer = viewModel.getPoint().toString()
        val userPointFromServer = SpManager.getString(SpManager.KEY_POINTS_FROM_SERVER, "0")
        val earnedPoints = SpManager.getDouble(SpManager.KEY_POINTS_EARNED, 0.0)
        val referEarnedPoints = SpManager.getDouble(SpManager.KEY_POINTS_REFER_EARNED, 0.0)
        val correctQuizAns = SpManager.getInt(SpManager.KEY_CORRECT_ANS, 0)
        Log.e("showUpdatedAciCoin", "userPointFromServer: $userPointFromServer")
        return (userPointFromServer.parseNumber() + earnedPoints + referEarnedPoints + correctQuizAns).roundTo()
    }

    private fun clearGivenCoin() {
        SpManager.saveDouble(SpManager.KEY_POINTS_EARNED, 0.0)
        SpManager.saveDouble(SpManager.KEY_POINTS_REFER_EARNED, 0.0)
        SpManager.saveInt(SpManager.KEY_QUIZ_COUNT, 0)
        SpManager.saveInt(SpManager.KEY_CORRECT_ANS, 0)
    }


    private fun calculateAndSavePoints(remainingTime: String) {

        val sdf = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("UTC")

        val remainingTimeInMillis = sdf.parse(remainingTime)?.time ?: 0
        val totalHoursInMillis = sdf.parse(Config.totalHours)?.time ?: 0

        val hoursGone: Double = (totalHoursInMillis.toDouble() - remainingTimeInMillis.toDouble()) / (1000 * 60 * 60)

        val pointsEarned = (hoursGone * Config.hourRate.parseNumber()).roundTo()
        SpManager.saveDouble(SpManager.KEY_POINTS_EARNED, pointsEarned.parseNumber())

        val prevReferEarnedPoints = SpManager.getDouble(SpManager.KEY_POINTS_REFER_EARNED, 0.0)
        val referPointsEarned = (prevReferEarnedPoints + Config.hourRate.parseNumber()/3600 * viewModel.myTeamMinerList.size * 0.10).roundTo()
        SpManager.saveDouble(SpManager.KEY_POINTS_REFER_EARNED, referPointsEarned.parseNumber())


        showUpdatedAciCoin()

       /* val secondsGone: Double = (totalHoursInMillis.parseNumber() - remainingTimeInMillis.parseNumber()) / 1000
        val minutesGone: Double = (totalHoursInMillis.parseNumber() - remainingTimeInMillis.parseNumber()) / (1000 * 60)

        Log.e("calculateAndSavePoints", "gone: hour: $hoursGone, min: $minutesGone, sec: $secondsGone")
        Log.e("calculateAndSavePoints", "miningPointsEarned: $pointsEarned")
        Log.e("calculateAndSavePoints", "prevReferEarnedPoints: $prevReferEarnedPoints")
        Log.e("calculateAndSavePoints", "referPointsEarned: $referPointsEarned")
        Log.e("calculateAndSavePoints", "-------------------------------------------------------------------")*/

    }

    private fun miningRewardProgress(miningHoursCountStr: String?) {
        var miningHoursCount = 0
        if (!miningHoursCountStr.isNullOrEmpty()) {
            miningHoursCount = miningHoursCountStr.toInt()
        }

        val maxHours = 720
        if (miningHoursCount > maxHours) miningHoursCount = maxHours
        val percentage = (miningHoursCount.toFloat() / maxHours * 100).toInt()
        binding.earnRewardProgressBar.progress = percentage
        binding.tvMiningHoursCount.text = " $miningHoursCount/$maxHours "
        if (miningHoursCount == maxHours) {
            binding.claimRewardLayout.visibility = View.VISIBLE
        }
    }

    private fun quizRewardProgress(quizCountStr: String?) {
        var quizCount = 0
        if (!quizCountStr.isNullOrEmpty()) {
            quizCount = quizCountStr.toInt()
        }
        val quizCountEarned = SpManager.getInt(SpManager.KEY_QUIZ_COUNT, 0)
        quizCount += quizCountEarned

        val maxQuizCount = 300
        if (quizCount > maxQuizCount) quizCount = maxQuizCount
        val percentageQuizCount = (quizCount.toFloat() / maxQuizCount * 100).toInt()
        binding.quizRewardProgressBar.progress = percentageQuizCount
        binding.tvQuizCount.text = " $quizCount/$maxQuizCount "
        if (quizCount == maxQuizCount) {
            binding.claimQuizRewardLayout.visibility = View.VISIBLE
        }
    }

    private fun newsFromWordpressBlog() {
        val api = RetrofitClient2.getApiService()
        val call = api.post2
        call.enqueue(object : Callback<List<Post2Item?>> {
            override fun onResponse(
                call: Call<List<Post2Item?>>,
                response: Response<List<Post2Item?>>
            ) {
                if (response.body() != null) {
                    binding.holderNewsTitle.show()
                    val postItemList2 = response.body()
                    val sortedTeamList = postItemList2?.take(5)
                    Log.e("sortedTeamList", "size: "+ sortedTeamList?.size )
                    binding.recyclerView.adapter = PostAdapter2(context, sortedTeamList)
                }
                else {
                    Log.e("sortedTeamList", "response is null" )
                    binding.holderNewsTitle.invisible()
                }
            }

            override fun onFailure(call: Call<List<Post2Item?>>, t: Throwable) {
                Log.e("sortedTeamList", "onFailure: "+ t.localizedMessage )
                binding.holderNewsTitle.invisible()
            }
        })
    }


    private fun startRippleEffect() {
        runOnUiThread {
            if (!binding.rippleEffect.isRippleAnimationRunning) {
                binding.centerImage.setColorFilter(
                    Color.argb(
                        255,
                        255,
                        255,
                        255
                    )
                )
                binding.rippleEffect.startRippleAnimation()
            }
        }
    }

    private fun stopRippleEffect() {
        runOnUiThread {
            if (binding.rippleEffect.isRippleAnimationRunning) {
                binding.centerImage.colorFilter = null //get back to previous logo color while stopping animation
                binding.rippleEffect.stopRippleAnimation() //stopping the animation
            }
        }

    }

    private fun showTapTarget2() {

        val tapTarget = TapTarget.forView(
            binding.ivMining,
            "Start Mining",
            "Click here to start your mining"
        ).outerCircleColor(R.color.teal_700) // Specify a color for the outer circle
        // Add other customization options here...

        val tapTargetView = TapTargetView.showFor(requireActivity(), tapTarget,
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view)
                    runOnUiThread {
                        startMining()
                    }
                }

                override fun onTargetDismissed(view: TapTargetView?, userInitiated: Boolean) {
                    super.onTargetDismissed(view, userInitiated)
                }
            })

        tapTargetView?.setOnClickListener {
            tapTargetView.dismiss(true)
        }
    }

    private fun showTapTarget() {
        val isTapShow = SpManager.getBoolean(SpManager.KEY_IS_TAP_TARGET_SHOW, true)
        Log.e("isTapShow", "showTapTarget: $isTapShow")
        if (!isTapShow) {
            return
        }

        SpManager.saveBoolean(SpManager.KEY_IS_TAP_TARGET_SHOW, false)
        val isTapShowNew = SpManager.getBoolean(SpManager.KEY_IS_TAP_TARGET_SHOW, true)
        Log.e("isTapShow", "isTapShowNew: $isTapShowNew")

        TapTargetView.showFor(requireActivity(),  // `this` is an Activity
            TapTarget.forView(
                binding.ivMining,
                "Start Mining",
                "Click here to start your mining"
            ) // All options below are optional
                .outerCircleColor(R.color.teal_700) // Specify a color for the outer circle
                .outerCircleAlpha(0.96f) // Specify the alpha amount for the outer circle
                .targetCircleColor(R.color.white) // Specify a color for the target circle
                .titleTextSize(20) // Specify the size (in sp) of the title text
                .titleTextColor(R.color.white) // Specify the color of the title text
                .descriptionTextSize(10) // Specify the size (in sp) of the description text
                .descriptionTextColor(R.color.white) // Specify the color of the description text
                .textColor(R.color.white) // Specify a color for both the title and description text
                .textTypeface(Typeface.SANS_SERIF) // Specify a typeface for the text
                .dimColor(R.color.black) // If set, will dim behind the view with 30% opacity of the given color
                .drawShadow(true) // Whether to draw a drop shadow or not
                .cancelable(BuildConfig.DEBUG) // true only in debug mode
                .tintTarget(true) // Whether to tint the target view's color
                .transparentTarget(false) // Specify whether the target is transparent (displays the content underneath)
                //                        .icon(Drawable)                     // Specify a custom drawable to draw as the target
                .targetRadius(60),  // Specify the target radius (in dp)
            object : TapTargetView.Listener() {
                // The listener can listen for regular clicks, long clicks or cancels
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view) // This call is optional
                    runOnUiThread {
                        SpManager.saveBoolean(SpManager.KEY_IS_TAP_TARGET_SHOW, true)
                        startMining()
                    }
                }

                override fun onTargetDismissed(view: TapTargetView?, userInitiated: Boolean) {
                    super.onTargetDismissed(view, userInitiated)
                }
            })
    }

    private fun refer() {
        val myReferCode = SpManager.getString(SpManager.KEY_MY_REFER_CODE, "")
        if (myReferCode == null || myReferCode.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Refer code not exist. Please contact with support!!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val appPackage = ClientProperties.getApplicationContext().packageName
        val appName = getString(R.string.app_name)
        val appPlayStoreLink =
            "https://play.google.com/store/apps/details?id=$appPackage"
        val message =
            "Join $appName using my referral code: $myReferCode\n\nDownload the app from Play Store: $appPlayStoreLink"
        val shareIntent = Intent()
        shareIntent.setAction(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)
        shareIntent.setType("text/plain")
        shareIntent.putExtra(Intent.EXTRA_TITLE, "Invite Friends")
        val pm = requireActivity().packageManager
        val activityList = pm.queryIntentActivities(shareIntent, 0)
        val chooser = Intent.createChooser(shareIntent, "Share via")
        if (activityList.size > 0) {
            startActivity(chooser)
        } else {
            Toast.makeText(requireContext(), "No apps available to share", Toast.LENGTH_SHORT)
                .show()
        }
    }


    private fun dialogShow(title: String, msg: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setCancelable(false)
        builder.setPositiveButton("Verify") { dialogInterface: DialogInterface?, i: Int ->
            viewModel.checkEmailVerifyStatus {
                dialogShow(
                    "Email verification",
                    "Your email is not verified. Please check your email and verify the mail."
                )
            }
        }
        builder.setNegativeButton(
            "Send Email"
        ) { dialog, which ->
            viewModel.sendEmailVerification()
            "Email verification sent. Please check your email".toast()
        }
        builder.setNeutralButton(
            "Cancel"
        ) { dialog, which ->
            val mAuth = FirebaseAuth.getInstance()
            mAuth.signOut()
            startActivity(Intent(context, LoginLayout::class.java))
            Toast.makeText(context, "Logout your Account", Toast.LENGTH_SHORT).show()
            clearAppData()
        }
        if (BuildConfig.DEBUG) {
            builder.create().show();
        } else {
            builder.create().show()
        }
    }

    private fun clearAppData() {
        try {
            (requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopMiningCountdown()
        viewModel.stopQuizCountdown()
    }


    private fun getLocationPermission() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        requestPermissionLauncher.launch(intent)
    }

    fun clearAppData(context: Context) {
        try {
            val packageManager = context.packageManager
            val packageName = context.packageName

            // Clear application data
            val clearData = Runtime.getRuntime().exec("pm clear $packageName")
            clearData.waitFor()

            // Restart the application
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            launchIntent?.let {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}