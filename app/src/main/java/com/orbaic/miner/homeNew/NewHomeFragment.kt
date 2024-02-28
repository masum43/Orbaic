package com.orbaic.miner.homeNew

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.orbaic.miner.AdMobAds
import com.orbaic.miner.BuildConfig
import com.orbaic.miner.R
import com.orbaic.miner.TeamMembersFragment
import com.orbaic.miner.allNews.AllNewsFragment
import com.orbaic.miner.common.Config
import com.orbaic.miner.common.Constants
import com.orbaic.miner.common.ErrorDialog
import com.orbaic.miner.common.ProgressDialog
import com.orbaic.miner.common.RetrofitClient2
import com.orbaic.miner.common.SpManager
import com.orbaic.miner.common.gone
import com.orbaic.miner.common.invisible
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
import kotlinx.coroutines.launch
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
    private val errorDialog by lazy { ErrorDialog(requireActivity()) }
    private val mobAds by lazy { AdMobAds(requireContext(), requireActivity()) }
    private var tapTargetShowing = false

    private val dataFetchActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
//            fetchData()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNewHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SpManager.init(requireContext())
        MobileAds.initialize(requireContext()) { mobAds.loadIntersAndRewardedAd() }
        prepareRv()
        initClicks()
        observeCountdownState()
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
        Log.e("timeStatus123", "fetchData: calling")
        progressDialog.show()
        viewModel.fetchData()
        viewModel.userData.observe(viewLifecycleOwner) { user ->
            Log.e("user123", "onDataChange: ${user?.name}")
            binding.aciCoin.text = user?.point?.roundTo()
            binding.aciCoin.tag = user?.point?.roundTo()
            binding.tvRate.text = "${Config.hourRate}/h ACI"
            SpManager.saveString(SpManager.KEY_MY_REFER_CODE, user?.referral)
            SpManager.saveString(SpManager.KEY_REFERRED_BY_UID, user?.referredBy)

            lifecycleScope.launch {
                val timeStatus = user?.isMiningWithin24Hours()
                Log.e("timeStatus123", "fetchData: $timeStatus")
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
        }

        viewModel.getMyTeam()
        viewModel.teamListLiveData.observe(viewLifecycleOwner) {
            val sortedTeamList = it.take(5)
            adapterTeam.updateData(sortedTeamList)
            val teamStatus = "${viewModel.myTeamMinerList.size}/${it.size}"
            binding.tvTeamStatus.text = teamStatus
        }
        newsFromWordpressBlog()
    }

    private fun initClicks() {
        binding.mining.setOnClickListener {
            startMining()
        }

        binding.learnAndEarn.setOnClickListener {
            val intent = Intent(context, QuizStartActivity::class.java)
            dataFetchActivityLauncher.launch(intent)
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
        mobAds.showRewardedVideo()
        setActiveStatus()
    }

    private fun setActiveStatus() {
        val mAuth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("users").child(mAuth.uid!!)
        val now = System.currentTimeMillis()
        val hashMap = HashMap<String, Any>()
        hashMap["miningStartTime"] = now.toString()
        hashMap["extra3"] = Constants.STATE_MINING_POINTS_NOT_GIVEN.toString()
        userRef.updateChildren(hashMap)

        val referralByUserId = SpManager.getString(SpManager.KEY_REFERRED_BY_UID, "")
        if (referralByUserId.isNotEmpty()) {
            val ref = database.getReference("referralUser")
                .child(referralByUserId).child(mAuth.uid!!)
            ref.child("status").setValue(now.toString())
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val now2 = Instant.now()
            val plus24Hours = now2.plusSeconds((24 * 60 * 60).toLong()) // Adding 24 hours in seconds
            //            Instant plus24Hours = now2.plusSeconds(5 * 60); // for testing
            val utcTime = DateTimeFormatter
                .ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .withZone(ZoneOffset.UTC)
                .format(plus24Hours)
            val reference = FirebaseDatabase.getInstance().getReference("usersToken")
            reference.child(mAuth.uid!!).child("timestamp").setValue(utcTime)
                .addOnSuccessListener { unused -> println(unused) }
        }

        fetchData()

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
                errorDialog.showTimeDiffWithServerError(errorMessage)
                progressDialog.dismiss()
            }
            Constants.STATE_MINING_POINTS_NOT_GIVEN -> {
                givePointsAndStartNewMiningSession()
            }
            Constants.STATE_MINING_FINISHED -> {
                startNewMiningStartSession()
            }
            else -> { // Mining start time is not within 24 hours
                startNewMiningStartSession()
            }
        }
    }

    private fun givePointsAndStartNewMiningSession() {
        val givenCoin = getGivenCoin()
        viewModel.giveUserMiningReferQuizPoint(givenCoin,
            onSuccess = {
                startNewMiningStartSession()
//                clearGivenCoin()
            },
            onFailure = {
                errorDialog.showTimeDiffWithServerError("Something went wrong. Please close the app and try again.")
            })
    }

    private fun startNewMiningStartSession() {
        errorDialog.dismissDialog()
        stopRippleEffect()
        progressDialog.dismiss()
        showTapTarget()
    }


    private fun observeCountdownState() {
        lifecycleScope.launch {
            viewModel.getMiningCountdownStateFlow().collect { state ->
                when (state) {
                    is CountdownState.Running -> {
                        Log.e("remainingTime", "state: Running")
                        val remainingTime = state.timeRemaining
                        binding.hourFragment.text = remainingTime
                        Log.e("remainingTime", "observeCountdownState: $remainingTime")
                        calculateAndSavePoints(remainingTime)
                    }
                    is CountdownState.Finished -> {
                        Log.e("remainingTime", "state: Finished")
                        givePointsAndStartNewMiningSession()
                    }
                    else -> {
                        // Handle other states if needed
                        Log.e("remainingTime", "state: else")
                        stopRippleEffect()
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
        binding.aciCoin.text = totalTokens.toString()
    }

    private fun getTotalCoin(): Double {
        val userPointFromServer = binding.aciCoin.tag.toString()
        val earnedPoints = SpManager.getDouble(SpManager.KEY_POINTS_EARNED, 0.0)
        val referEarnedPoints = SpManager.getDouble(SpManager.KEY_POINTS_REFER_EARNED, 0.0)
        val correctQuizAns = SpManager.getInt(SpManager.KEY_CORRECT_ANS, 0)
        val totalTokens = (userPointFromServer.toDouble() + earnedPoints + referEarnedPoints + correctQuizAns).roundTo()
        return totalTokens
    }

    private fun getGivenCoin(): Double {
        val earnedPoints = SpManager.getDouble(SpManager.KEY_POINTS_EARNED, 0.0)
        val referEarnedPoints = SpManager.getDouble(SpManager.KEY_POINTS_REFER_EARNED, 0.0)
        val correctQuizAns = SpManager.getInt(SpManager.KEY_CORRECT_ANS, 0)
        val totalTokens = (earnedPoints + referEarnedPoints + correctQuizAns).roundTo()
        return totalTokens
    }

    private fun clearGivenCoin() {
        SpManager.saveDouble(SpManager.KEY_POINTS_EARNED, 0.0)
        SpManager.saveDouble(SpManager.KEY_POINTS_REFER_EARNED, 0.0)
        SpManager.saveInt(SpManager.KEY_CORRECT_ANS, 0)
    }


    private fun calculateAndSavePoints(remainingTime: String) {
        val totalHours = "24:00:00"
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("UTC")

        try {
            val remainingTimeInMillis = sdf.parse(remainingTime)?.time ?: 0
            val totalHoursInMillis = sdf.parse(totalHours)?.time ?: 0

            Log.e("calculateAndSavePoints", "totalHoursInMillis: $totalHoursInMillis")
            Log.e("calculateAndSavePoints", "remainingTimeInMillis: $remainingTimeInMillis")

            val hoursGone: Double = (totalHoursInMillis.toDouble() - remainingTimeInMillis.toDouble()) / (1000 * 60 * 60)
            Log.e("calculateAndSavePoints", "hoursGone: $hoursGone")
            val pointsEarned = hoursGone * 0.045
            SpManager.saveDouble(SpManager.KEY_POINTS_EARNED, pointsEarned)
            Log.e("calculateAndSavePoints", "pointsEarned: $pointsEarned")

            val prevReferEarnedPoints = SpManager.getDouble(SpManager.KEY_POINTS_REFER_EARNED, 0.0)
            Log.e("calculateAndSavePoints", "prevReferEarnedPoints: $prevReferEarnedPoints")
            val referPointsEarned = prevReferEarnedPoints + 0.045/60 * viewModel.myTeamMinerList.size
            SpManager.saveDouble(SpManager.KEY_POINTS_REFER_EARNED, referPointsEarned)
            Log.e("calculateAndSavePoints", "referPointsEarned: $referPointsEarned")

            showUpdatedAciCoin()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun miningRewardProgress(miningHoursCountStr: String?) {
        if (miningHoursCountStr.isNullOrEmpty()) {
            return
        }
        var miningHoursCount: Int = miningHoursCountStr.toInt()
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
        if (quizCountStr.isNullOrEmpty()) {
            return
        }
        var quizCount: Int = quizCountStr.toInt()
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
                ) //change the logo color while staring animation
                binding.rippleEffect.startRippleAnimation() //starting the animation
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

    private fun showTapTarget() {
        if (tapTargetShowing) return

        tapTargetShowing = true
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
                        startMining()
                    }
                }

                override fun onTargetDismissed(view: TapTargetView?, userInitiated: Boolean) {
                    super.onTargetDismissed(view, userInitiated)
                    tapTargetShowing = false
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
}