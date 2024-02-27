package com.orbaic.miner.homeNew

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
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
import com.orbaic.miner.BuildConfig
import com.orbaic.miner.R
import com.orbaic.miner.TeamMembersFragment
import com.orbaic.miner.allNews.AllNewsFragment
import com.orbaic.miner.common.Config
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

class NewHomeFragment : Fragment() {
    private lateinit var binding : FragmentNewHomeBinding
    private val viewModel: NewHomeViewModel by viewModels()
    private val adapterTeam by lazy { MyTeamAdapter() }
    private val progressDialog by lazy { ProgressDialog.Builder(requireContext()).build() }
    private val errorDialog by lazy { ErrorDialog(requireActivity()) }


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
            binding.tvRate.text = "${Config.hourRate}/h ACI"
            SpManager.saveString(SpManager.KEY_MY_REFER_CODE, user?.referral)

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
        }
        newsFromWordpressBlog()
    }

    private fun initClicks() {
        binding.mining.setOnClickListener {
            miningLogic()
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
            viewModel.claimReward(Config.miningRewardedTokenCode, Config.quizRewardedBonusToken,
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

    private fun miningLogic() {

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
            1 -> { // Mining start time is within 24 hours
                viewModel.startMiningCountdown(miningStartTime.toLong())
                startRippleEffect()
                errorDialog.dismissDialog()
                progressDialog.dismiss()
            }
            2 -> { // Time difference between server and device
                viewModel.stopMiningCountdown()
                viewModel.stopQuizCountdown()
                stopRippleEffect()
                val errorMessage = timeStatus.message ?: "Unknown error"
                errorDialog.showTimeDiffWithServerError(errorMessage)
                progressDialog.dismiss()
            }
            else -> { // Mining start time is not within 24 hours
                errorDialog.dismissDialog()
                stopRippleEffect()
                showTapTarget()
                progressDialog.dismiss()
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
                        stopRippleEffect()
                        showTapTarget()
                    }
                    else -> {
                        // Handle other states if needed
                        Log.e("remainingTime", "state: else")
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
//                        SpManager.saveBoolean(SpManager.KEY_IS_TAP_TARGET_SHOW, true)
//                        miningLogic()
                    }
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