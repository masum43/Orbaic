package com.orbaic.miner;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.orbaic.miner.common.Constants;
import com.orbaic.miner.myTeam.GridBindAdapter;
import com.orbaic.miner.myTeam.Team;
import com.orbaic.miner.quiz.QuizStartActivity;
import com.orbaic.miner.wordpress.Post;
import com.orbaic.miner.wordpress.PostAdapter;
import com.orbaic.miner.wordpress.RetrofitClient;
import com.orbaic.miner.wordpress.WordpressData;
import com.skyfishjy.library.RippleBackground;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//22955
public class Home extends Fragment {


    public Home() {
        // Required empty public constructor
    }

    public CountDownTimer count;
    ConstraintLayout transfer;
    TextView learnEarn;

    private double Coin = 0.0F;
    Timer time = new Timer();
    TimerTask timerTask;
    public long mEndTime, timeLeftInMillis, oldMilli = 0, newMillis = 0, sleepTime = 0, endTime = -1;
    private static long START_TIME_IN_MILLIS = 86400000;
    TextView hr, AciCoin;
    TextView tvTeamStatus;
    LinearLayout available, quizWaitingLayout;
    private ImageView referral, facebook, twitter, telegram, instagram;
    private ImageView white;
    private LinearLayout mining;
    private RippleBackground rippleEffect;
    ImageView rippleCenterImage;
    private RecyclerView postList, teamRecyclerView;

    Task<Void> currentUser;

    FirebaseUser user;
    String referralStatus, referralBy, myReferCode, miningStatus, miningStartTime;
    private List<Post> postItemList;
    FirebaseData data = new FirebaseData();
    List<Team> teamList = new ArrayList<>();
    List<Team> onMiningDataList = new ArrayList<>();

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_2, container, false);
        readData();
        AdMobAds mobAds = new AdMobAds(getContext(), getActivity());
        MobileAds.initialize(requireContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                mobAds.loadRewardedAd();
            }
        });
        initViews(view);

        user = FirebaseAuth.getInstance().getCurrentUser();
        currentUser = FirebaseAuth.getInstance().getCurrentUser().reload();
        checkEmailVerifyStatus();

        initClicks(mobAds);

        newsFromWordpressBlog(true);

        return view;

    }

    private void initClicks(AdMobAds mobAds) {
        mining.setOnClickListener(v -> {
            getMiningStatus(miningStatus -> {
                if (miningStatus.equals(Constants.STATUS_OFF)) {
                    if (!onMiningDataList.isEmpty()) {
                        Dialog dialog = new Dialog(requireActivity());
                        dialog.setContentView(R.layout.dialog_extra_point);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        TextView tvNotice = dialog.findViewById(R.id.tvNotice);
                        tvNotice.setText("Your "+onMiningDataList.size() + " team member is mining now. So you will get extra : " + (10*onMiningDataList.size()) +"%.");
                        dialog.findViewById(R.id.okButton).setOnClickListener(view -> {
                            dialog.dismiss();
                            startMining(mobAds);

                        });
                        dialog.show();
                    }
                    else {
                        startMining(mobAds);
                    }

                }
                else {
                    miningAlreadyRunningWarning();
                }
            });


        });

        transfer.setOnClickListener(v -> {
            PushNotificationExtra notificationExtra = new PushNotificationExtra(getContext());
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
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

    private void startMining(AdMobAds mobAds) {
        mobAds.showRewardedVideo();
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
        postList = view.findViewById(R.id.recyclerView);
        teamRecyclerView = view.findViewById(R.id.rvMyTeam);
        tvTeamStatus = view.findViewById(R.id.tvTeamStatus);
    }

    private void startRippleEffect() {
        if (!rippleEffect.isRippleAnimationRunning()){
            rippleCenterImage.setColorFilter(Color.argb(255, 255, 255, 255)); //change the logo color while staring animation
            rippleEffect.startRippleAnimation(); //starting the animation
        }
    }

    private void stopRippleEffect() {
        if (rippleEffect.isRippleAnimationRunning()){
            rippleCenterImage.setColorFilter(null); //get back to previous logo color while stopping animation
            rippleEffect.stopRippleAnimation(); //stopping the animation
        }
    }

    //user team referral user active status
    private void setActiveStatus() {
        long active = System.currentTimeMillis() + START_TIME_IN_MILLIS;
        String s = String.valueOf(active);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refUser = database.getReference("users")
                .child(mAuth.getUid());
        long now = System.currentTimeMillis();
        refUser.child("miningStartTime").setValue(String.valueOf(now));

        if (referralStatus.equals("OFF")) {
            DatabaseReference ref = database.getReference("referralUser")
                    .child(referralBy).child(mAuth.getUid());
            ref.child("status").setValue(s);
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
                postList.setHasFixedSize(true);
                postList.setLayoutManager(new LinearLayoutManager(getContext()));

                List<Post> firstFiveItems = new ArrayList<>();
                if (postItemList.size() >= 10) {
                    firstFiveItems.addAll(postItemList.subList(0, 10));
                } else {
                    firstFiveItems.addAll(postItemList);
                }
                postList.setAdapter(new PostAdapter(getContext(), firstFiveItems));

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

    // Mining system
    private void addPoints() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (internetConnectionCheck()) {
                    int myTeamMiningCount = onMiningDataList.size();
                    if (myTeamMiningCount != 0) {
                        Coin = Coin + ((0.000012 * 5) + (0.000012 * 5 * 0.10 * myTeamMiningCount)) ;
                    }
                    else {
                        Coin = Coin + (0.000012 * 5);
                    }

                    data.sentData(String.valueOf(Coin));
                    System.out.println(Coin);
                    addPoints();
                } else {
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
        //Toast.makeText(getContext(),"Internet error", Toast.LENGTH_SHORT).show();
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

    }

    private void startOnFun() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("users").child(mAuth.getUid());

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String point = snapshot.child("point").getValue().toString();
                Coin = Double.valueOf(point);
                String format = String.format(Locale.getDefault(), "%.5f", Coin);
                AciCoin.setText(format);

                //Learn and Earn Enable
                String enableTime = snapshot.child("extra1").getValue().toString();
                endTime = Long.parseLong(enableTime);
                long currentTime = System.currentTimeMillis();
                if (currentTime > endTime) {
                    quizWaitingLayout.setVisibility(View.GONE);
                    available.setVisibility(View.VISIBLE);
                } else {
                    quizWaitingLayout.setVisibility(View.VISIBLE);
                    available.setVisibility(View.GONE);
                }

                SharedPreferences preferences = getContext().getSharedPreferences("perf", Context.MODE_PRIVATE);
                timeLeftInMillis = preferences.getLong("millis", timeLeftInMillis);
                oldMilli = timeLeftInMillis;
                //System.out.println(System.currentTimeMillis());
                updateText();
                mEndTime = preferences.getLong("lastMillis", 0);
                //System.out.println(timeLeftInMillis +" "+ mEndTime);
                timeLeftInMillis = mEndTime - System.currentTimeMillis();
                newMillis = timeLeftInMillis;
                if (newMillis > 0) {
                    sleepTime = oldMilli - newMillis;
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            Coin = (double) (Coin + ((sleepTime / 1000) * 0.000012));
                            data.sentData(String.valueOf(Coin));
                            //AciCoin.setText(post.getPoint());
                            //System.out.println("sleeping"+ Coin);
                        }
                    };
                    time.schedule(timerTask, 1000);
                }
                //System.out.println("endTime"+timeLeftInMillis);
                if (timeLeftInMillis < 0) {
                    timeLeftInMillis = 0;
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            Coin = (double) (Coin + ((oldMilli / 1000) * 0.000012));
                            if (Coin >= 0) {
                                data.sentData(String.valueOf(Coin));
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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onStop() {
        super.onStop();
        mEndTime = System.currentTimeMillis() + timeLeftInMillis;
        SharedPreferences preferences = getContext().getSharedPreferences("perf", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putLong("millis", timeLeftInMillis);
        edit.putLong("lastMillis", mEndTime);
        edit.apply();

        timerTask.cancel();

        //System.out.println(timeLeftInMillis +" "+ mEndTime);

        if (count != null) {
            count.cancel();
        }
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
                }
                else miningStartTime = "-1";

                String miningStatus = checkMiningStatus(miningStartTime);
                if (miningStatus.equals(Constants.STATUS_ON)) {
                    startRippleEffect();
                }
                else {
                    stopRippleEffect();
                }
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
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String point = snapshot.child("point").getValue().toString();
                referralStatus = snapshot.child("referralButton").getValue().toString();
                if (snapshot.child("miningStartTime").exists()) {
                    miningStartTime = snapshot.child("miningStartTime").getValue().toString();
                }
                else miningStartTime = "-1";

                String miningStatus = checkMiningStatus(miningStartTime);
                if (miningStatus.equals(Constants.STATUS_ON)) {
                    startRippleEffect();
                }
                else {
                    stopRippleEffect();
                }

                // System.out.println(referralStatus);
                Coin = Double.valueOf(point);
                String format = String.format(Locale.getDefault(), "%.5f", Coin);
                AciCoin.setText(format);


                if (referralStatus.equals("OFF")) {
                    referralBy = snapshot.child("referredBy").getValue().toString();
                } else {
                    //Toast.makeText(getContext(), "You are not user code", Toast.LENGTH_SHORT).show();
                }
                myReferCode = snapshot.child("referral").getValue().toString();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!myReferCode.isEmpty()) {
                    getMyTeam(myReferCode);
                }
            }
        }, 1500);
    }

    private String checkMiningStatus(String miningStartTime) {
        long now = System.currentTimeMillis();
        long miningStartTimeLong = Long.parseLong(miningStartTime);
        long timeElapsed = now - miningStartTimeLong;

        Log.e("checkMiningStatus", "now: "+ now );
        Log.e("checkMiningStatus", "miningStartTimeLong: "+ miningStartTimeLong );
        Log.e("checkMiningStatus", "timeElapsed: "+ timeElapsed );

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

    private void getMyTeam(String myReferCode) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference();
        Query referredUsersQuery = databaseRef.child("users")
                .orderByChild("referredBy")
                .equalTo(myReferCode);

        referredUsersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teamList.clear();
                onMiningDataList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Log.e("getMyTeam", "userSnapshot: "+ userSnapshot);
                    String userId = userSnapshot.child("id").getValue(String.class);
                    String userName = userSnapshot.child("name").getValue(String.class);
                    String userEmail = userSnapshot.child("email").getValue(String.class);
                    String miningStartTime = "-1";
                    if (userSnapshot.child("miningStartTime").exists()) {
                        miningStartTime = userSnapshot.child("miningStartTime").getValue(String.class);
                    }
                    Log.e("getMyTeam", "miningStartTime: "+miningStartTime );

                    String miningStatus = checkMiningStatus(miningStartTime);
                    Log.e("getMyTeam", "miningStatus: "+miningStatus );

                    teamList.add(new Team(userId, userName, userEmail, "", miningStartTime, miningStatus));

                }

                teamRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));
                GridBindAdapter adapter = new GridBindAdapter(getActivity(), teamList);
                teamRecyclerView.setAdapter(adapter);

                for (Team miningData : teamList) {
                    if (miningData.getMiningStatus().equals(Constants.STATUS_ON)) {
                        onMiningDataList.add(miningData);
                    }
                }

                tvTeamStatus.setText(onMiningDataList.size()+"/"+teamList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur
            }
        });
    }


    private boolean internetConnectionCheck() {
        try {
            String cmd = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(cmd).waitFor() == 0);
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

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
//        builder.create().show();
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


}