package com.orbaic.miner.home;


import static com.unity3d.services.core.misc.Utilities.runOnUiThread;
import static com.unity3d.services.core.properties.ClientProperties.getApplicationContext;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.orbaic.miner.AdMobAds;
import com.orbaic.miner.FirebaseData;
import com.orbaic.miner.LoginLayout;
import com.orbaic.miner.MainActivity2;
import com.orbaic.miner.MiningStatusCallback;
import com.orbaic.miner.PushNotificationExtra;
import com.orbaic.miner.R;
import com.orbaic.miner.ReferralDataRecive;
import com.orbaic.miner.SendDataFirebaseDatabase;
import com.orbaic.miner.TeamMembersFragment;
import com.orbaic.miner.TeamReferral;
import com.orbaic.miner.WhitePaper;
import com.orbaic.miner.allNews.AllNewsFragment;
import com.orbaic.miner.common.Constants;
import com.orbaic.miner.common.GetNetTime;
import com.orbaic.miner.common.RetrofitClient2;
import com.orbaic.miner.common.SpManager;
import com.orbaic.miner.databinding.FragmentHome2Binding;
import com.orbaic.miner.myTeam.GridBindAdapter;
import com.orbaic.miner.myTeam.Team;
import com.orbaic.miner.quiz.QuizStartActivity;
import com.orbaic.miner.wordpress.Post;
import com.orbaic.miner.wordpress.PostAdapter;
import com.orbaic.miner.wordpress.PostAdapter2;
import com.orbaic.miner.wordpress.RetrofitClient;
import com.orbaic.miner.wordpress.WordpressData;
import com.skyfishjy.library.RippleBackground;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//22955
public class Home extends Fragment {
    private FragmentHome2Binding binding;

    public Home() {
        // Required empty public constructor
    }

    public CountDownTimer count;
    ConstraintLayout transfer;
    TextView learnEarn;

    private double Coin = 0.0F;
    Timer time = new Timer();
    TimerTask timerTask;
    public long mEndTime, quizTimestamp = 0,  timeLeftInMillis, oldMilli = 0, newMillis = 0, sleepTime = 0, endTime = -1;
    private static long START_TIME_IN_MILLIS = 86400000;
    TextView hr, AciCoin;
    TextView tvTeamStatus;
    LinearLayout available, quizWaitingLayout;
    private ImageView referral, facebook, twitter, telegram, instagram;
    private ImageView white;
    private LinearLayout mining;
    private RippleBackground rippleEffect;
    ImageView rippleCenterImage;
    private RecyclerView rvNews, teamRecyclerView;

    Task<Void> currentUser;

    FirebaseUser user;
    String referralStatus, myReferCode, miningStatus, miningStartTime;
    String referralByUserId = "";
    private List<Post> postItemList;
    private List<Post2.Post2Item> postItemList2;
    FirebaseData data = new FirebaseData();
    List<Team> teamList = new ArrayList<>();
    List<Team> onMiningDataList = new ArrayList<>();
    TextView tvQuizCountDown;
    GetNetTime netTime = new GetNetTime();
    TextView tvRate;
    ProgressBar waitingQuizProgressbar, earnRewardProgressBar, quizRewardProgressBar;
    TextView tvMiningHoursCount, tvQuizCount;
    private Boolean isMyTeamLoaded = false;
    private HomeViewModel viewModel;
    AdMobAds mobAds;
    ImageView ivMining;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRewardedTokensRef;
    DatabaseReference userRef;


    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHome2Binding.inflate(getLayoutInflater(), container, false);
//        View view = inflater.inflate(R.layout.fragment_home_2, container, false);
        View view = binding.getRoot();

        SpManager.init(requireActivity());

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        mobAds = new AdMobAds(getContext(), getActivity());
        MobileAds.initialize(requireContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                mobAds.loadIntersAndRewardedAd();
            }
        });

        initViews(view);

        user = FirebaseAuth.getInstance().getCurrentUser();
        currentUser = FirebaseAuth.getInstance().getCurrentUser().reload();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRewardedTokensRef = database.getReference("my_rewarded_tokens").child(mAuth.getCurrentUser().getUid());
        userRef = database.getReference("users").child(mAuth.getUid());
        checkEmailVerifyStatus();

        initClicks();
        newsFromWordpressBlog2(true);

        updateTokenInDatabase();

        return view;

    }

    private void tapDone() {
        if(timeLeftInMillis <= 0){
            SpManager.saveBoolean(SpManager.KEY_IS_TAP_TARGET_SHOW, false);
            showTapTarget();
        }else {
            SpManager.saveBoolean(SpManager.KEY_IS_TAP_TARGET_SHOW, true);
            showTapTarget();
        }
    }

    private void showTapTarget() {
        boolean isTapDone = SpManager.getBoolean(SpManager.KEY_IS_TAP_TARGET_SHOW, false);
        if (isTapDone){
            return;
        }
        TapTargetView.showFor(requireActivity(),                 // `this` is an Activity
                TapTarget.forView(ivMining, "Start Mining", "Click here to start your mining")
                        // All options below are optional
                        .outerCircleColor(R.color.teal_700)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .titleTextSize(20)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.white)      // Specify the color of the title text
                        .descriptionTextSize(10)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.white)  // Specify the color of the description text
                        .textColor(R.color.white)            // Specify a color for both the title and description text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
//                        .icon(Drawable)                     // Specify a custom drawable to draw as the target
                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                        runOnUiThread(() -> {
                            SpManager.saveBoolean(SpManager.KEY_IS_TAP_TARGET_SHOW, true);
                            miningLogic();
                        });

                    }
                });


    }

    private void quizCountDown(String enableTime) {
        long currentTime;

        quizTimestamp = netTime.getNetTime(getContext());
        if (!netTime.isError()) {
            currentTime = quizTimestamp;
            System.out.println("current time from net: " + currentTime);
        }else {
            currentTime = System.currentTimeMillis();
            System.out.println("current time from net: " + currentTime);
        }



        long timeDifference = Long.parseLong(enableTime) - currentTime;


        new CountDownTimer(timeDifference, 1000) {
            public void onTick(long millisUntilFinished) {
                // Calculate remaining hours, minutes, and seconds
                long hours = millisUntilFinished / (60 * 60 * 1000);
                long minutes = (millisUntilFinished % (60 * 60 * 1000)) / (60 * 1000);
                long seconds = (millisUntilFinished % (60 * 1000)) / 1000;

                // Display the remaining time as a countdown
                String remainingTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                // Update a TextView or any other UI element to show the remaining time.
                tvQuizCountDown.setText(remainingTime);


                long currentTimeMillis = System.currentTimeMillis();
                long futureEnableTimeMillis = Long.parseLong(enableTime); // Replace with your future enable time
                long twelveHoursInMillis = 12 * 60 * 60 * 1000; // 12 hours in milliseconds
                long timeDifferenceMillis = futureEnableTimeMillis - currentTimeMillis;
                int progress = 100 - ((int) ((timeDifferenceMillis * 100) / twelveHoursInMillis));
                // Ensure the progress is within the valid range [0, 100]
                progress = Math.max(0, Math.min(100, progress));
                waitingQuizProgressbar.setProgress(progress);

                if (progress == 100) {
                    quizWaitingLayout.setVisibility(View.GONE);
                    available.setVisibility(View.VISIBLE);
                }
            }

            public void onFinish() {
                // The countdown timer has finished, you can start the quiz or perform any other action.
            }
        }.start();
    }

    private void initClicks() {
        mining.setOnClickListener(v -> {
            miningLogic();
        });

        transfer.setOnClickListener(v -> {
            PushNotificationExtra notificationExtra = new PushNotificationExtra(getContext());
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    //get token
                    String uid = FirebaseAuth.getInstance().getUid();
                    String token = task.getResult();
                    System.out.println("Device Token : " + token);

                    //get Time
                    LocalDateTime currentTime = LocalDateTime.now().plusDays(1);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String oneDayPlus = currentTime.format(formatter);

                    //token and timestamp map
                    Map<String, String> fcmData = new HashMap<>();
                    fcmData.put("fcmToken", token);
                    fcmData.put("timestamp", oneDayPlus);
                    System.out.println(fcmData);

                    //send FCM token and Timestamp
                    SendDataFirebaseDatabase database = new SendDataFirebaseDatabase();
                    database.sendUserData(uid, fcmData);
                    notificationExtra.sendNotification(token, "Test", "body");
                }
            });
            /*Toast.makeText(getContext(), "Coming Soon", Toast.LENGTH_SHORT).show();*/
        });

        learnEarn.setOnClickListener(v -> {
            //startActivity(new Intent(getContext(), QuizStartActivity.class));
            if (endTime != -1) {
                long currentTime = System.currentTimeMillis();
                if (currentTime > endTime) {
                    startActivity(new Intent(getContext(), QuizStartActivity.class));
                } else {
                    Toast.makeText(getContext(), "After Every 12 Hours", Toast.LENGTH_SHORT).show();
                }
            }
        });

        referral.setOnClickListener(v -> {
            Fragment newFragment = new TeamReferral();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, newFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        white.setOnClickListener(v -> {

            Fragment newFragment = new WhitePaper();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, newFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        });

        facebook.setOnClickListener(v -> {
            String url = "https://www.facebook.com/orbaic/";

            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        });

        twitter.setOnClickListener(v -> {
            String url = "https://twitter.com/Orbaicproject?s=08";

            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        telegram.setOnClickListener(v -> {
            String url = "https://t.me/OrbaicEnglish";

            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        });

        instagram.setOnClickListener(v -> {
            String url = "https://www.instagram.com/orbaicproject/";
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        binding.holderRefer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refer();
            }
        });

        binding.tvTeamMore.setOnClickListener(view -> getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new TeamMembersFragment())
                .addToBackStack(null)
                .commit());

        binding.tvNewsMore.setOnClickListener(view -> getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new AllNewsFragment())
                .addToBackStack(null)
                .commit());

    }

    private void miningLogic() {
        if (timeLeftInMillis <= 0) {
            if (!onMiningDataList.isEmpty()) {
                Dialog dialog = new Dialog(requireActivity());
                dialog.setContentView(R.layout.dialog_extra_point);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView tvNotice = dialog.findViewById(R.id.tvNotice);
                tvNotice.setText("Your " + onMiningDataList.size() + " team member is mining now. So you will get extra : " + (10 * onMiningDataList.size()) + "%.");
                dialog.findViewById(R.id.okButton).setOnClickListener(view -> {
                    dialog.dismiss();
                    startMining();

                });
                dialog.show();
            } else {
                startMining();
            }

        } else {
            miningAlreadyRunningWarning();
        }
    }

    private void miningAlreadyRunningWarning() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_mining_warning);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.findViewById(R.id.okButton).setOnClickListener(view -> {
            dialog.dismiss();


        });
        dialog.show();
    }

    private void startMining() {
        mobAds.showRewardedVideo();
        data.changeMiningRewardStatus("1");
        startRippleEffect();

        runClock();
        setActiveStatus();
        addPoints();
    }

    private void checkEmailVerifyStatus() {
        currentUser.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                System.out.println(user.isEmailVerified());
                if (user.isEmailVerified()) {
                    /* Toast.makeText(getContext(), "email Verified", Toast.LENGTH_SHORT).show();*/
                } else {
                    dialogShow("Email verification", "Your email is not verified. Please check your email and verify the mail.");
                }
            }
        });
    }

    private void initViews(View view) {
        learnEarn = view.findViewById(R.id.learnAndEarn);
        tvMiningHoursCount = view.findViewById(R.id.tvMiningHoursCount);
        tvQuizCount = view.findViewById(R.id.tvQuizCount);
        available = view.findViewById(R.id.learnAvailable);
        quizWaitingLayout = view.findViewById(R.id.quizWaitingLayout);
        transfer = view.findViewById(R.id.trans);
        mining = view.findViewById(R.id.mining);
        rippleEffect = view.findViewById(R.id.rippleEffect);
        rippleCenterImage = view.findViewById(R.id.centerImage);

        instagram = view.findViewById(R.id.instagram_h);
        telegram = view.findViewById(R.id.telegram_h);
        twitter = view.findViewById(R.id.twitter_h);
        facebook = view.findViewById(R.id.facebookIcon_h);
        referral = view.findViewById(R.id.refe);

        white = view.findViewById(R.id.white_paper);

        hr = view.findViewById(R.id.hour_fragment);
        AciCoin = view.findViewById(R.id.aci_coin);
        rvNews = view.findViewById(R.id.recyclerView);
        teamRecyclerView = view.findViewById(R.id.rvMyTeam);
        tvTeamStatus = view.findViewById(R.id.tvTeamStatus);
        tvQuizCountDown = view.findViewById(R.id.tvQuizCountDown);
        waitingQuizProgressbar = view.findViewById(R.id.quizWaitingProgressBar);
        earnRewardProgressBar = view.findViewById(R.id.earnRewardProgressBar);
        quizRewardProgressBar = view.findViewById(R.id.quizRewardProgressBar);
        tvRate = view.findViewById(R.id.tvRate);
        ivMining = view.findViewById(R.id.ivMining);
    }

    private void startRippleEffect() {
        runOnUiThread(() -> {
            if (!rippleEffect.isRippleAnimationRunning()) {
                rippleCenterImage.setColorFilter(Color.argb(255, 255, 255, 255)); //change the logo color while staring animation
                rippleEffect.startRippleAnimation(); //starting the animation
            }
        });

    }

    private void stopRippleEffect() {
        runOnUiThread(() -> {
            if (rippleEffect.isRippleAnimationRunning()) {
                rippleCenterImage.setColorFilter(null); //get back to previous logo color while stopping animation
                rippleEffect.stopRippleAnimation(); //stopping the animation
            }
        });

    }

    //user team referral user active status
    private void setActiveStatus() {
        long active = System.currentTimeMillis() + START_TIME_IN_MILLIS;
        String s = String.valueOf(active);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users")
                .child(mAuth.getUid());
        long now = System.currentTimeMillis();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("miningStartTime", String.valueOf(now));
//        int miningHours = viewModel.getMiningHoursCount();
//        hashMap.put("mining_count", String.valueOf(miningHours + 24));


        userRef.updateChildren(hashMap);

        if (!referralByUserId.isEmpty()) {
            DatabaseReference ref = database.getReference("referralUser")
                    .child(referralByUserId).child(mAuth.getUid());
            ref.child("status").setValue(String.valueOf(now));

        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Instant now2 = Instant.now();
            Instant plus24Hours = now2.plusSeconds(24 * 60 * 60); // Adding 24 hours in seconds
//            Instant plus24Hours = now2.plusSeconds(5 * 60); // for testing

            String utcTime = DateTimeFormatter
                    .ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                    .withZone(ZoneOffset.UTC)
                    .format(plus24Hours);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usersToken");

            reference.child(mAuth.getUid()).child("timestamp").setValue(utcTime)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            System.out.println(unused);
                        }
                    });
        }


    }

    //news from wordpress blog
    private void newsFromWordpressBlog(boolean withProgress) {

        WordpressData api = RetrofitClient.getApiService();
        Call<List<Post>> call = api.getPost();

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(getString(R.string.progressdialog_title));
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.progressdialog_message));

        if (withProgress) {
            progressDialog.show();
        }

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                Log.d("RetrofitResponse", "Status Code " + response.code());
                postItemList = response.body();
                rvNews.setHasFixedSize(true);
                rvNews.setLayoutManager(new LinearLayoutManager(getContext()));

                List<Post> firstFiveItems = new ArrayList<>();
                if (postItemList.size() >= 5) {
                    firstFiveItems.addAll(postItemList.subList(0, 5));
                } else {
                    firstFiveItems.addAll(postItemList);
                }
                Collections.sort(firstFiveItems, (o1, o2) -> {
                    if (o1.getFeatured_media() == 1 && o2.getFeatured_media() != 1) {
                        return -1; // o1 comes first
                    } else if (o1.getFeatured_media() != 1 && o2.getFeatured_media() == 1) {
                        return 1; // o2 comes first
                    } else {
                        return 0; // maintain the original order if both or neither have featured_media == 1
                    }
                });

                Log.e("enque1122", "onResponse: "+ new Gson().toJson(firstFiveItems));
                rvNews.setAdapter(new PostAdapter(getContext(), firstFiveItems));

                if (withProgress) {
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    };
                    time.schedule(timerTask, 1000);
                }


            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.d("RetrofitResponse", "Error");
                if (withProgress) {
                    progressDialog.dismiss();
                }
            }
        });

    }

    private void newsFromWordpressBlog2(boolean withProgress) {

        WordpressData api = RetrofitClient2.getApiService();
        Call<List<Post2.Post2Item>> call = api.getPost2();

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(getString(R.string.progressdialog_title));
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.progressdialog_message));

        if (withProgress) {
            progressDialog.show();
        }

        call.enqueue(new Callback<List<Post2.Post2Item>>() {
            @Override
            public void onResponse(Call<List<Post2.Post2Item>> call, Response<List<Post2.Post2Item>> response) {
                Log.d("RetrofitResponse", "Status Code " + response.code());
                postItemList2 = response.body();
                rvNews.setHasFixedSize(true);
                rvNews.setLayoutManager(new LinearLayoutManager(getContext()));

                List<Post2.Post2Item> firstFiveItems = new ArrayList<>();
                if (postItemList2.size() >= 5) {
                    firstFiveItems.addAll(postItemList2.subList(0, 5));
                } else {
                    firstFiveItems.addAll(postItemList2);
                }
/*                Collections.sort(firstFiveItems, (o1, o2) -> {
                    if (o1.getFeatured_media() == 1 && o2.getFeatured_media() != 1) {
                        return -1; // o1 comes first
                    } else if (o1.getFeatured_media() != 1 && o2.getFeatured_media() == 1) {
                        return 1; // o2 comes first
                    } else {
                        return 0; // maintain the original order if both or neither have featured_media == 1
                    }
                });*/

                //Log.e("enque1122", "onResponse: "+ new Gson().toJson(firstFiveItems));
                rvNews.setAdapter(new PostAdapter2(getContext(), firstFiveItems));

                if (withProgress) {
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    };
                    time.schedule(timerTask, 1000);
                }


            }

            @Override
            public void onFailure(Call<List<Post2.Post2Item>> call, Throwable t) {
                //Log.d("RetrofitResponse", "Error");
                if (withProgress) {
                    progressDialog.dismiss();
                }
            }
        });

    }

    // Mining system
    private void addPoints() {
        Log.e("DATA_READ", "addPoints: ");
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (internetConnectionCheck()) {
                    int myTeamMiningCount = onMiningDataList.size();
                    double hourRate = 0.045;
                    if (myTeamMiningCount != 0) {
                        Coin = Coin + ((0.000012 * 5) + (0.000012 * 5 * 0.10 * myTeamMiningCount));
                        hourRate = hourRate + hourRate * 0.10 * myTeamMiningCount;
                    } else {
                        Coin = Coin + (0.000012 * 5);
                    }
                    double finalHourRate = hourRate;
                    runOnUiThread(() -> tvRate.setText(finalHourRate + "/h ACI"));


                    data.addMiningPoints(String.valueOf(Coin));
                    System.out.println(Coin);
                    Log.e("COIN_UPDATE", "Coin1: " + Coin);
                    String format = String.format(Locale.getDefault(), "%.5f", Coin);
                    runOnUiThread(() -> AciCoin.setText(format));
                    addPoints();
                } else {
                    Log.e("COIN_UPDATE", "run: No internet connection");
                    stop();
                }
            }
        };
        time.schedule(timerTask, 5000);


    }

    //stop timer and timer task
    private void stop() {
        timerTask.cancel();
        System.out.println("error");
        count.cancel();
        stopRippleEffect();
    }

    //mining time countdown
    private void runClock() {

        count = new CountDownTimer(START_TIME_IN_MILLIS, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMillis = l;
                updateText();
            }

            @Override
            public void onFinish() {
                mining.setVisibility(View.VISIBLE);
                START_TIME_IN_MILLIS = 86400000;
                timerTask.cancel();
            }
        }.start();
    }

    //countdown time update in text which is in left side of top
    private void updateText() {
        int hour = (int) (timeLeftInMillis / 1000) / 3600;
        int minute = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormat = String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minute, seconds);
        hr.setText(timeFormat);

    }

    @Override
    public void onStart() {
        super.onStart();
        readData();
        startOnFun();
        getMyTeam();

    }

    private void startOnFun() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("users").child(mAuth.getUid());

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("DATA_READ", "startOnFun");
                String point = snapshot.child("point").getValue().toString();
                Coin = Double.valueOf(point);
                String format = String.format(Locale.getDefault(), "%.5f", Coin);
                AciCoin.setText(format);

                //Start - Learn and Earn Enable
                String enableTime = snapshot.child("extra1").getValue().toString();
                endTime = Long.parseLong(enableTime);

                // new update tomal
                long currentTime;

                quizTimestamp = netTime.getNetTime(getContext());
                System.out.println("current time from net: " + quizTimestamp);

                if (!netTime.isError()) {
                    currentTime = quizTimestamp;
                    System.out.println("current time from net: " + currentTime);
                }else {
                    currentTime = System.currentTimeMillis();
                    System.out.println("current time from local: " + currentTime);
                }


                if (currentTime > endTime) {
                    quizWaitingLayout.setVisibility(View.GONE);
                    available.setVisibility(View.VISIBLE);
                } else {
                    quizWaitingLayout.setVisibility(View.VISIBLE);
                    available.setVisibility(View.GONE);
                    quizCountDown(enableTime);
                }
                //End - Learn and Earn Enable


                String miningHours = "0";
                if (snapshot.hasChild("mining_count")) {
                    miningHours = snapshot.child("mining_count").getValue().toString();
                }
                viewModel.setMiningHoursCount(Integer.parseInt(miningHours));

                String qzCountStr = "0";
                if (snapshot.hasChild("qz_count")) {
                    qzCountStr = snapshot.child("qz_count").getValue().toString();
                }
                viewModel.setQuizCount(Integer.parseInt(qzCountStr));

                //miningRewardStatus
                String miningRewardStatus = snapshot.child("extra2").getValue().toString();
                if (miningRewardStatus.equals("1")) {
                    miningStartTime = snapshot.child("miningStartTime").getValue().toString();
                    String miningStatus = checkMiningStatus(miningStartTime);
                    if (miningStatus.equals(Constants.STATUS_OFF)) {
                        data.changeMiningRewardStatusWithMiningCount("0",
                                String.valueOf(viewModel.getMiningHoursCount() + 24));
                    }
                }
                //miningRewardStatus

                SharedPreferences preferences = getContext().getSharedPreferences("perf", Context.MODE_PRIVATE);
                timeLeftInMillis = preferences.getLong("millis", timeLeftInMillis);
                Log.e("BUGS_123", "timeLeftInMillis: "+ timeLeftInMillis);
                oldMilli = timeLeftInMillis;
                Log.e("BUGS_123", "oldMilli: "+ oldMilli);
                updateText();
                mEndTime = preferences.getLong("lastMillis", 0);
                Log.e("BUGS_123", "mEndTime: "+ mEndTime);
                timeLeftInMillis = mEndTime - System.currentTimeMillis();
                Log.e("BUGS_123", "timeLeftInMillis: "+ timeLeftInMillis);
                Log.e("BUGS_123", "System.currentTimeMillis(): "+ System.currentTimeMillis());
                newMillis = timeLeftInMillis;
                Log.e("BUGS_123", "newMillis: "+ newMillis);
                Log.e("COIN_UPDATE", "newMillis: " + newMillis);
                if (newMillis > 0) {
                    Log.e("BUGS_123", "newMillis > 0");
                    sleepTime = oldMilli - newMillis;
                    Log.e("BUGS_123", "sleepTime: "+ sleepTime);
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            Coin = Coin + ((sleepTime / 1000) * 0.000012);
                            data.addMiningPoints(String.valueOf(Coin));
                            String format = String.format(Locale.getDefault(), "%.5f", Coin);
                            Log.e("COIN_UPDATE", "Coin1: " + Coin);
                            requireActivity().runOnUiThread(() -> AciCoin.setText(format));
                        }
                    };
                    time.schedule(timerTask, 1000);
                }
                //System.out.println("endTime"+timeLeftInMillis);
                Log.e("COIN_UPDATE", "timeLeftInMillis: " + timeLeftInMillis);
                Log.e("BUGS_123", "timeLeftInMillis: "+ timeLeftInMillis);
                if (timeLeftInMillis < 0) {
                    timeLeftInMillis = 0;
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            Coin = Coin + ((oldMilli / 1000) * 0.000012);
                            if (Coin >= 0) {
                                data.addMiningPoints(String.valueOf(Coin));
                                String format = String.format(Locale.getDefault(), "%.5f", Coin);
                                Log.e("COIN_UPDATE", "Coin2: " + Coin);
                                requireActivity().runOnUiThread(() -> AciCoin.setText(format));
                                //System.out.println("finish" + Coin);
                            }
                        }
                    };
                    time.schedule(timerTask, 4000);
                    updateText();
                    START_TIME_IN_MILLIS = 86400000;
                } else {
                    START_TIME_IN_MILLIS = timeLeftInMillis;
//                    mining.setVisibility(View.GONE);
                    startRippleEffect();
                    runClock();
                    addPoints();
                }
                tapDone();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG", "onCancelled: " + error.getMessage());

            }
        });


    }

    @Override
    public void onStop() {
        super.onStop();
        mEndTime = System.currentTimeMillis() + timeLeftInMillis; //actual end time of the mining
        SharedPreferences preferences = getContext().getSharedPreferences("perf", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        Log.e("BUG123", "millis: "+ timeLeftInMillis);
        edit.putLong("millis", timeLeftInMillis);
        edit.putLong("lastMillis", mEndTime);
        edit.apply();

       if (timerTask != null) timerTask.cancel();

        //System.out.println(timeLeftInMillis +" "+ mEndTime);

        if (count != null) {
            count.cancel();
        }
        stopRippleEffect();
    }

    public void getMiningStatus(MiningStatusCallback callback) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("users").child(mAuth.getUid());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("miningStartTime").exists()) {
                    miningStartTime = snapshot.child("miningStartTime").getValue().toString();
                } else miningStartTime = "-1";

                String miningStatus = checkMiningStatus(miningStartTime);
                /*if (miningStatus.equals(Constants.STATUS_ON)) {
                    startRippleEffect();
                } else {
                    stopRippleEffect();
                }*/
                callback.onMiningStatusChanged(miningStatus);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onMiningStatusChanged(Constants.STATUS_OFF);
            }
        });
    }

    public void readData() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference myRef = firebaseDatabase.getReference("users").child(mAuth.getUid());

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("DATA_READ", "readData");
                String name = snapshot.child("name").getValue().toString();
                String email = snapshot.child("email").getValue().toString();
                String point = snapshot.child("point").getValue().toString();

                String miningHours = "0";
                if (snapshot.hasChild("mining_count")) {
                    miningHours = snapshot.child("mining_count").getValue().toString();
                }
                viewModel.setMiningHoursCount(Integer.parseInt(miningHours));

                String qzCountStr = "0";
                if (snapshot.hasChild("qz_count")) {
                    qzCountStr = snapshot.child("qz_count").getValue().toString();
                }
                viewModel.setQuizCount(Integer.parseInt(qzCountStr));

                String profile_image = "";
                if (snapshot.hasChild("profile_image")) {
                    profile_image = snapshot.child("profile_image").getValue().toString();
                }

                referralStatus = snapshot.child("referralButton").getValue().toString();
                if (snapshot.child("miningStartTime").exists()) {
                    miningStartTime = snapshot.child("miningStartTime").getValue().toString();
                } else miningStartTime = "-1";

                Log.e("miningStartTime", "onDataChange: " + miningStartTime);

                String miningStatus = checkMiningStatus(miningStartTime);
                /*if (miningStatus.equals(Constants.STATUS_ON)) {
                    startRippleEffect();
                } else {
                    stopRippleEffect();
                    showTapTarget();
                }*/

                // System.out.println(referralStatus);
                Coin = Double.valueOf(point);
                String format = String.format(Locale.getDefault(), "%.5f", Coin);
                AciCoin.setText(format);

                if (snapshot.child("referredBy").exists()) {
                    referralByUserId = snapshot.child("referredBy").getValue().toString();
                }

                myReferCode = snapshot.child("referral").getValue().toString();

                boolean isDailyTaskDone = SpManager.isDailyTaskDone();
                Log.e("isDailyTaskDone", "isDailyTaskDone: " + isDailyTaskDone);
                if (!isDailyTaskDone) {
                    Log.e("isDailyTaskDone", "onDataChange");
                    setMyReferKey(name, myReferCode);
                }

                if (!isMyTeamLoaded) {
                    isMyTeamLoaded = true;
                    MainActivity2 mainActivity = (MainActivity2) getActivity();
                    if (mainActivity != null) {
                        mainActivity.updateHeader(profile_image, name, email);
                    }
                }

                miningRewardProgress();
                quizRewardProgress();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void quizRewardProgress() {
        int quizCount = viewModel.getQuizCount();
        int maxQuizCount = 300;
        if (quizCount > maxQuizCount) quizCount = maxQuizCount;

        int percentageQuizCount = (int) ((float) quizCount / maxQuizCount * 100);
        quizRewardProgressBar.setProgress(percentageQuizCount);
        tvQuizCount.setText(" " + quizCount + "/" + maxQuizCount + " ");
        if (quizCount == maxQuizCount) {
            binding.claimQuizRewardLayout.setVisibility(View.VISIBLE);
            binding.claimQuizRewardLayout.setOnClickListener(v -> {
                binding.claimQuizRewardLayout.setVisibility(View.GONE);
                myRewardedTokensRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot mSnap : dataSnapshot.getChildren()) {
                                MyRewardedTokenItem rewardedTokenItem = mSnap.getValue(MyRewardedTokenItem.class);
                                if (rewardedTokenItem.getCode().equals("SHIB")) {
                                    long rewardedBalance = Long.parseLong(rewardedTokenItem.getBalance());
                                    Long updatedBalance = rewardedBalance + 2000;

                                    myRewardedTokensRef.child(rewardedTokenItem.getId().toString())
                                            .child("balance").setValue(String.valueOf(updatedBalance))
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("TAG", "Rewarded balance updated!");
                                                Toast.makeText(requireContext(), "Claimed successfully. Check in wallet", Toast.LENGTH_LONG).show();
                                                binding.tvQuizCount.setText(" 0 / 300");
                                                data.addQuizCount("0");
                                            })
                                            .addOnFailureListener(e -> {
                                                // Failed to update the balance
                                                Log.e("TAG", "Failed to update rewarded balance!", e);
                                                // Handle the error
                                                binding.claimQuizRewardLayout.setVisibility(View.VISIBLE);
                                            });
                                }
                            }

                        } else {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", 1);
                            hashMap.put("name", "SHIBA INU (SHIB)");
                            hashMap.put("code", "SHIB");
                            hashMap.put("balance", "2000");
                            hashMap.put("icon", "https://firebasestorage.googleapis.com/v0/b/orbaic-6832f.appspot.com/o/wallet_reward_token_shib.png?alt=media&token=de3a647a-ed26-44f3-90e7-28db8441290c");
                            myRewardedTokensRef.child("1").setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(requireContext(), "Claimed successfully. Check in wallet", Toast.LENGTH_LONG).show();
                                            binding.tvMiningHoursCount.setText(" 0 / 300");
                                            data.addMiningCount("0");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            binding.claimRewardLayout.setVisibility(View.VISIBLE);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("TAG", "Failed to update rewarded balance! " + error.getDetails(), error.toException());

                    }
                });
            });
        }
    }

    private void miningRewardProgress() {
        int miningHoursCount = viewModel.getMiningHoursCount();
        int maxHours = 720;
        if (miningHoursCount > maxHours) miningHoursCount = maxHours;

        int percentage = (int) ((float) miningHoursCount / maxHours * 100);
        earnRewardProgressBar.setProgress(percentage);
        tvMiningHoursCount.setText(" " + miningHoursCount + "/" + maxHours + " ");
        if (miningHoursCount == maxHours) {
            binding.claimRewardLayout.setVisibility(View.VISIBLE);
            binding.claimRewardLayout.setOnClickListener(v -> {
                binding.claimRewardLayout.setVisibility(View.GONE);
                myRewardedTokensRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot mSnap : dataSnapshot.getChildren()) {
                                MyRewardedTokenItem rewardedTokenItem = mSnap.getValue(MyRewardedTokenItem.class);
                                if (rewardedTokenItem.getCode().equals("SHIB")) {
                                    long rewardedBalance = Long.parseLong(rewardedTokenItem.getBalance());
                                    Long updatedBalance = rewardedBalance + 3000;

                                    myRewardedTokensRef.child(rewardedTokenItem.getId().toString())
                                            .child("balance").setValue(String.valueOf(updatedBalance))
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("TAG", "Rewarded balance updated!");
                                                Toast.makeText(requireContext(), "Claimed successfully. Check in wallet", Toast.LENGTH_LONG).show();
                                                binding.tvMiningHoursCount.setText(" 0 / 720 hours");
                                                data.addMiningCount("0");
                                            })
                                            .addOnFailureListener(e -> {
                                                // Failed to update the balance
                                                Log.e("TAG", "Failed to update rewarded balance!", e);
                                                // Handle the error
                                                binding.claimRewardLayout.setVisibility(View.VISIBLE);
                                            });
                                }
                            }

                        } else {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", 1);
                            hashMap.put("name", "SHIBA INU (SHIB)");
                            hashMap.put("code", "SHIB");
                            hashMap.put("balance", "3000");
                            hashMap.put("icon", "https://firebasestorage.googleapis.com/v0/b/orbaic-6832f.appspot.com/o/wallet_reward_token_shib.png?alt=media&token=de3a647a-ed26-44f3-90e7-28db8441290c");
                            myRewardedTokensRef.child("1").setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(requireContext(), "Claimed successfully. Check in wallet", Toast.LENGTH_LONG).show();
                                            binding.tvMiningHoursCount.setText(" 0 / 720 hours");
                                            data.addMiningCount("0");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            binding.claimRewardLayout.setVisibility(View.VISIBLE);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            });
        }
    }

    private String checkMiningStatusTeam(String miningStartTime) {
        long now = System.currentTimeMillis();
        long miningStartTimeLong = Long.parseLong(miningStartTime);
        long timeElapsed = now - miningStartTimeLong;

        Log.e("checkMiningStatus", "now: " + now);
        Log.e("checkMiningStatus", "miningStartTimeLong: " + miningStartTimeLong);
        Log.e("checkMiningStatus", "timeElapsed: " + timeElapsed);

        if (timeElapsed >= 24 * 60 * 60 * 1000) {
            // If more than 24 hours have elapsed, do something
            // Your code here
            return Constants.STATUS_OFF;
        } else {
            // If less than 24 hours have elapsed, do something else
            // Your code here
            return Constants.STATUS_ON;
        }
    }

    private String checkMiningStatus(String miningStartTime) {
        long now = System.currentTimeMillis();
        long miningStartTimeLong = Long.parseLong(miningStartTime);
        long timeElapsed = now - miningStartTimeLong;

        Log.e("checkMiningStatus", "now: " + now);
        Log.e("checkMiningStatus", "miningStartTimeLong: " + miningStartTimeLong);
        Log.e("checkMiningStatus", "timeElapsed: " + timeElapsed);

        if (timeElapsed >= 24 * 60 * 60 * 1000) {
            // If more than 24 hours have elapsed, do something
            // Your code here
            return Constants.STATUS_OFF;
        } else {
            // If less than 24 hours have elapsed, do something else
            // Your code here
            return Constants.STATUS_ON;
        }
    }


    private void updateTokenInDatabase() {
        if (mAuth.getCurrentUser() != null) {
            SpManager.init(requireContext());
            String fcmToken = SpManager.getString(SpManager.KEY_FCM_TOKEN, "");
            String fcmNewToken = SpManager.getString(SpManager.KEY_FCM_NEW_TOKEN, "");

            if (fcmToken.isEmpty() || !fcmNewToken.equals(fcmToken)) {
                setToken();
            }
        }

    }

    private void setToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult();
                        // Handle the token, you can print or use it as needed
                        System.out.println("FCM Token: " + token);
                        String userId = mAuth.getCurrentUser().getUid();
                        Log.e("userId", "updateTokenInDatabase: "+userId );
                        DatabaseReference tokensRef = FirebaseDatabase.getInstance().getReference("usersToken");
                        tokensRef.child(userId).child("fcmToken").setValue(token).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    SpManager.saveString(SpManager.KEY_FCM_TOKEN, token);
                                    SpManager.saveString(SpManager.KEY_FCM_NEW_TOKEN, token);
                                }
                            }
                        });
                    } else {
                        // Handle the error
                        System.out.println("Error fetching FCM token: " + task.getException());
                    }
                });
    }

    private void getMyTeam() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference ref = database.getReference("referralUser").child(mAuth.getUid());
        ref.addValueEventListener(new ValueEventListener() {

            public void onDataChange(@NonNull DataSnapshot snapshot) {
                teamList.clear();
                onMiningDataList.clear();
                Log.e("DATA_READ", "getMyTeam");
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    ReferralDataRecive data = dataSnapshot.getValue(ReferralDataRecive.class);
                    Log.e("getMyTeam2", "key: " + dataSnapshot.getKey());
                    String miningStatus = checkMiningStatusTeam(data.getStatus());
                    teamList.add(new Team(dataSnapshot.getKey(), data.getName(), "", "", data.getStatus(), miningStatus));
                }

                teamRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));
                if (teamList.size() <= 5) {
                    GridBindAdapter adapter = new GridBindAdapter(getActivity(), teamList);
                    teamRecyclerView.setAdapter(adapter);
                }
                else {
                    List<Team> sortedTeamList = new ArrayList<>();
                    for (int i = 0; i< 5; i++) {
                        sortedTeamList.add(teamList.get(i));
                    }
                    GridBindAdapter adapter = new GridBindAdapter(getActivity(), sortedTeamList);
                    teamRecyclerView.setAdapter(adapter);
                }


                for (Team miningData : teamList) {
                    if (miningData.getMiningStatus().equals(Constants.STATUS_ON)) {
                        onMiningDataList.add(miningData);
                    }
                }

                tvTeamStatus.setText(onMiningDataList.size() + "/" + teamList.size());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("" + error);
            }
        });


    }


    private boolean internetConnectionCheck() {
        try {
            InetAddress address = InetAddress.getByName("google.com");
            return address.isReachable(5000); // Timeout in milliseconds
        } catch (IOException e) {
            return false;
        }
    }

/*    private boolean internetConnectionCheck() {
        try {
            String cmd = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(cmd).waitFor() == 0);
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            return false;
        }

    }*/

    private void dialogShow(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (currentUser == null) {
                    Toast.makeText(getContext(), "You are not a user. Please connect with Orbaic Support", Toast.LENGTH_SHORT).show();
                }
                currentUser = FirebaseAuth.getInstance().getCurrentUser().reload();


                currentUser.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        System.out.println(user.isEmailVerified());
                        if (user.isEmailVerified()) {
                            Toast.makeText(getContext(), "email Verified", Toast.LENGTH_SHORT).show();
                        } else {
                            dialogShow("Email verification", "Your email is not verified. Please check your email and verify the mail.");
                        }
                    }
                });


            }
        });
        builder.setNegativeButton("Send Email", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                user.sendEmailVerification();
                dialogShow("Email verification", "Your email is not verified. Please check your email and verify the mail.");
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                startActivity(new Intent(getContext(), LoginLayout.class));
                Toast.makeText(getContext(), "Logout your Account", Toast.LENGTH_SHORT).show();
                clearAppData();
            }
        });
        if (com.orbaic.miner.BuildConfig.DEBUG) {
            //builder.create().show();
        } else {
            builder.create().show();
        }

    }


    private void clearAppData() {
        try {
            // clearing app data
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                ((ActivityManager) getActivity().getSystemService(getActivity().ACTIVITY_SERVICE)).clearApplicationUserData(); // note: it has a return value!
            } else {
                String packageName = getActivity().getApplicationContext().getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear " + packageName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setMyReferKey(String name, String code) {
        if (code.isEmpty()) {
            return;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference referKeys = database.getReference("referKeys");
        Map<String, String> referKeyMap = new HashMap<>();
        referKeyMap.put("name", name);
        referKeyMap.put("userId", mAuth.getCurrentUser().getUid().toString());

        Log.e("setMyReferKeyError", "getUid: " + mAuth.getCurrentUser().getUid().toString());
        Log.e("setMyReferKeyError", "code: " + code);

        referKeys.child(code).setValue(referKeyMap).addOnCompleteListener(task2 -> {
            if (task2.isSuccessful()) {
                SpManager.makeDailyTaskDone();
            } else {
                String errorMessage = task2.getException().getMessage();
                //Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("setMyReferKeyError", "task2: " + errorMessage);
            }
        });
    }

    private void refer() {
        if (myReferCode == null || myReferCode.isEmpty()) {
            Toast.makeText(requireContext(), "Refer code not exist. Please contact with support!!", Toast.LENGTH_SHORT).show();
            return;
        }
        String referralCode = myReferCode;
        String appPackage = getApplicationContext().getPackageName();
        String appName = getString(R.string.app_name);
        String appPlayStoreLink = "https://play.google.com/store/apps/details?id=" + appPackage;

        String message = "Join " + appName + " using my referral code: " + referralCode + "\n\n" + "Download the app from Play Store: " + appPlayStoreLink;

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        shareIntent.setType("text/plain");

        shareIntent.putExtra(Intent.EXTRA_TITLE, "Invite Friends");

        PackageManager pm = requireActivity().getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);

        Intent chooser = Intent.createChooser(shareIntent, "Share via");

        if (activityList.size() > 0) {
            startActivity(chooser);
        } else {
            Toast.makeText(requireContext(), "No apps available to share", Toast.LENGTH_SHORT).show();
        }

    }

}