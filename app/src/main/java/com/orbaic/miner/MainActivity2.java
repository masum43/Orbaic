package com.orbaic.miner;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.orbaic.miner.auth.LoginLayout;
import com.orbaic.miner.common.Config;
import com.orbaic.miner.common.Constants;
import com.orbaic.miner.common.CustomDialog;
import com.orbaic.miner.common.SpManager;
import com.orbaic.miner.homeNew.NewHomeFragment;
import com.orbaic.miner.interfaces.NavigationDrawerInterface;
import com.orbaic.miner.myTeam.TeamFragment;
import com.orbaic.miner.profile.Profile;
import com.orbaic.miner.settings.SettingsFragment;
import com.orbaic.miner.support.SupportFragment;
import com.orbaic.miner.wallet.WalletFragment;

import java.util.HashMap;

public class MainActivity2 extends AppCompatActivity implements NavigationDrawerInterface {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ImageView hamBurgIcon;
    View header;
    CircularImageView profileIcon;
    BottomNavigationView bottomNavigationView;
    ImageView btnEditProfile;
    private ConsentInformation consentInformation;
    private boolean isClickable = true;
    private CustomDialog customDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        customDialog = new CustomDialog(this);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationViewId);
        header = navigationView.getHeaderView(0);
        hamBurgIcon = findViewById(R.id.hamBurgMenuId);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        profileIcon = findViewById(R.id.profileIcon);
        setNavigationDrawerMenu();
        setBottomNavigationMenu();


        btnEditProfile = header.findViewById(R.id.btnEditProfile);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        loadFragment(new NewHomeFragment());

        refer();
        initClicks();
        gdpr();

        pointSeparation();

    }

    private void refer() {
        if (getIntent().hasExtra("referBy")) {
            String name = getIntent().getStringExtra("name");
            String referBy = getIntent().getStringExtra("referBy");
            Log.e("addIntoReferTeam", "name: " + name);
            Log.e("addIntoReferTeam", "referBy: " + referBy);
            if (referBy != null && !referBy.isEmpty()) {
                addIntoReferTeam(name, referBy);
            }

        }
    }

    private void initClicks() {
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new Profile());
            }
        });

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new Profile());
            }
        });
    }

    private void addIntoReferTeam(String name, String referredBy) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference();
        Query referredUsersQuery = databaseRef.child("users")
                .orderByChild("referral")
                .equalTo(referredBy);

        referredUsersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userId = "";
                String point = "0";
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Log.e("addIntoReferTeam", "userSnapshot: " + userSnapshot);
                    userId = userSnapshot.getKey();
                    if (userSnapshot.hasChild("point")) {
                        point = userSnapshot.child("point").getValue().toString();
                    }

                }

                Log.e("addIntoReferTeam", "userId: " + userId);
                if (userId != null && !userId.isEmpty()) {
                    DatabaseReference referralRef = database.getReference("referralUser");
                    HashMap<String, String> map = new HashMap<>();
                    map.put("name", name);
                    map.put("status", "-1");
                    referralRef.child(userId).child(mAuth.getUid().toString()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.e("addIntoReferTeam", "task: isSuccessful");
                            }
                        }
                    });

                    if (!point.isEmpty()) {
                        double pointWithBonus = Double.parseDouble(point) +3 ;
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("point", String.valueOf(pointWithBonus));

                        DatabaseReference userRef = database.getReference("users");
                        userRef.child(userId).updateChildren(hashMap);

                        HashMap<String, Object> hashMap2 = new HashMap<>();
                        hashMap2.put("point", "3");
                        userRef.child(mAuth.getUid().toString()).updateChildren(hashMap2);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur
            }
        });
    }

    private void setBottomNavigationMenu() {
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    if (isClickable) {
                        switch (item.getItemId()) {
                            case R.id.homeMenu:
                                loadFragment(new NewHomeFragment());
                                break;
                            case R.id.walletMenu:
                                loadFragment(new WalletFragment());
                                break;
                            case R.id.supportMenu:
                                loadFragment(new SupportFragment());
                                break;
                            case R.id.teamMenu:
                                loadFragment(new TeamFragment());
                                break;
                        }
                        disableClickForDelay();
                        return true;
                    }
                    return false;
                });
    }

    private void disableClickForDelay() {
        isClickable = false;
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            isClickable = true;
        }, 1000); // 1000 milliseconds (1 second) delay
    }

/*    private void setBottomNavigationMenu() {
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.homeMenu:
                            loadFragment(new Home());
                            return true;
                        case R.id.walletMenu:
                            loadFragment(new WalletFragment());
                            return true;
                        case R.id.supportMenu:
                            loadFragment(new SupportFragment());
                            return true;
                        case R.id.teamMenu:
                            loadFragment(new TeamFragment());
                            return true;
                    }
                    return false;
                });
    }*/

    private void setNavigationDrawerMenu() {
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Log.e("DrawerMenu", "setNavigationDrawerMenu: " + id);
            if (id == R.id.home) {
                loadFragment(new NewHomeFragment());
            } else if (id == R.id.menuMiningRules) {
                Fragment fragment = new CommonWebview();
                Bundle bundle = new Bundle();
                bundle.putString("url", getString(R.string.url_mining_rules));
                fragment.setArguments(bundle);
                loadFragment(fragment);
            } else if (id == R.id.menuWhitePaper) {
                Fragment fragment = new CommonWebview();
                Bundle bundle = new Bundle();
                bundle.putString("url", getString(R.string.url_white_paper));
                fragment.setArguments(bundle);
                loadFragment(fragment);

            } else if (id == R.id.menuFaq) {
                Fragment fragment = new CommonWebview();
                Bundle bundle = new Bundle();
                bundle.putString("url", getString(R.string.url_faq));
                fragment.setArguments(bundle);
                loadFragment(fragment);
            }
            else if (id == R.id.menuTermsAndConditions) {
                Fragment fragment = new CommonWebview();
                Bundle bundle = new Bundle();
                bundle.putString("url", getString(R.string.url_terms));
                fragment.setArguments(bundle);
                loadFragment(fragment);
            } else if (id == R.id.menuPrivacyPolicy) {
                Fragment fragment = new CommonWebview();
                Bundle bundle = new Bundle();
                bundle.putString("url", getString(R.string.url_privacy_policy));
                fragment.setArguments(bundle);
                loadFragment(fragment);
            } else if (id == R.id.referralTeam) {
                loadFragment(new TeamReferral());
            } else if (id == R.id.profile) {
                loadFragment(new Profile());
            } else if (id == R.id.menuSettings) {
                loadFragment(new SettingsFragment());
            } else if (id == R.id.logout) {
                checkStateForLogOut();
            }
            else {
                Toast.makeText(MainActivity2.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });


        hamBurgIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }

        });

        findViewById(R.id.holdeLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkStateForLogOut();
            }
        });

        TextView tvVersion = findViewById(R.id.tvVersion);
        tvVersion.setText("Version: "+ com.orbaic.miner.BuildConfig.VERSION_NAME);
    }

    private void checkStateForLogOut() {
        int miningStatus = SpManager.getInt(SpManager.KEY_MINER_STATUS, Constants.STATE_MINING_FINISHED);
        if (miningStatus == Constants.STATE_MINING_ON_GOING) {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_logout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView tvNotice = dialog.findViewById(R.id.tvNotice);
            tvNotice.setText(R.string.warning_logout);
            LinearLayout holderBg = dialog.findViewById(R.id.holderBg);
            holderBg.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
            dialog.findViewById(R.id.cancelButton).setOnClickListener(view -> {
                dialog.dismiss();

            });
            dialog.findViewById(R.id.okButton).setOnClickListener(view -> {
                dialog.dismiss();
                logout();

            });
            dialog.show();
        }
        else {
            logout();
        }

    }

    private void logout() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users").child(mAuth.getCurrentUser().getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("miningStartTime", "-1");
        hashMap.put("extra3", String.valueOf(Constants.STATE_MINING_FINISHED));
        userRef.updateChildren(hashMap);

        String referralByUserId = SpManager.getString(SpManager.KEY_REFERRED_BY_UID, "");
        if (!referralByUserId.isEmpty()) {
            DatabaseReference ref = database.getReference("referralUser")
                    .child(referralByUserId).child(mAuth.getCurrentUser().getUid());
            ref.child("status").setValue("-1");
        }

        SpManager.saveDouble(SpManager.KEY_POINTS_EARNED, 0.0);
        SpManager.saveDouble(SpManager.KEY_POINTS_REFER_EARNED, 0.0);
        SpManager.saveInt(SpManager.KEY_QUIZ_COUNT, 0);
        SpManager.saveInt(SpManager.KEY_CORRECT_ANS, 0);

        SpManager.clearPreferences();

        mAuth.signOut();
        startActivity(new Intent(MainActivity2.this, LoginLayout.class));
        Toast.makeText(MainActivity2.this, "Logout your Account", Toast.LENGTH_SHORT).show();
        clearAppData();
    }


    private void loadFragment(Fragment f) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, f);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                getSupportFragmentManager().popBackStack();
            } else if (isTaskRoot()) {
                backClick();
            } else {
                super.onBackPressed();
            }
        }

    }

    private void backClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("Do you want to exit");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }


    private void clearAppData() {
        try {
            ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE))
                    .clearApplicationUserData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void updateHeader(String avatarUrl, String username, String userEmail) {
        NavigationView navigationView = findViewById(R.id.navigationViewId);
        View headerView = navigationView.getHeaderView(0);
        TextView usernameTextView = headerView.findViewById(R.id.tvUserName);
        TextView userEmailTextView = headerView.findViewById(R.id.tvUserEmail);
        CircularImageView ivAvater = headerView.findViewById(R.id.ivAvater);

        Glide.with(MainActivity2.this)
                .load(avatarUrl)
                .error(R.drawable.demo_avatar2)
                .into(ivAvater);
        usernameTextView.setText(username);
        userEmailTextView.setText(userEmail);

        Glide.with(MainActivity2.this)
                .load(avatarUrl)
                .error(R.drawable.demo_avatar2)
                .into(profileIcon);
    }


    private void gdpr() {
        ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(this)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .addTestDeviceHashedId("CCCB127F53A56AAC680FBB669EC85DB8")
                .build();

        ConsentRequestParameters params;
        if (BuildConfig.DEBUG) {
            params = new ConsentRequestParameters.Builder()
                    .setConsentDebugSettings(debugSettings)
                    .build();
        } else {
            params = new ConsentRequestParameters.Builder()
                    .setTagForUnderAgeOfConsent(false)
                    .build();
        }

        consentInformation = UserMessagingPlatform.getConsentInformation(this);
        if (BuildConfig.DEBUG) {
            consentInformation.reset();
        }

        consentInformation.requestConsentInfoUpdate(this, params,
                () -> UserMessagingPlatform.loadAndShowConsentFormIfRequired(MainActivity2.this, loadAndShowError -> {
                    // Consent gathering failed.
                    Log.w("TAG", String.format("%s: %s",
                            loadAndShowError != null ? loadAndShowError.getErrorCode() : "",
                            loadAndShowError != null ? loadAndShowError.getMessage() : ""));

                    // Consent has been gathered.
                    if (consentInformation.canRequestAds()) {
                        initializeMobileAdsSdk();
                    }
                }),
                requestConsentError -> {
                    // Consent gathering failed.
                    Log.w("TAG", String.format("%s: %s",
                            requestConsentError != null ? requestConsentError.getErrorCode() : "",
                            requestConsentError != null ? requestConsentError.getMessage() : ""));
                });

        // Check if you can initialize the Google Mobile Ads SDK in parallel
        // while checking for new consent information. Consent obtained in
        // the previous session can be used to request ads.
        if (consentInformation.canRequestAds()) {
            initializeMobileAdsSdk();
        }
    }

    private void initializeMobileAdsSdk() {
    }



    private void pointSeparation() {
        boolean isPointSeparated = SpManager.getBoolean(SpManager.KEY_POINT_SEPARATED, false);
        Log.e("isPointSeparated", "isPointSeparated: "+ isPointSeparated);
        if (isPointSeparated) return;



        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();
        FirebaseDatabase database =  FirebaseDatabase.getInstance();

        DatabaseReference userIdRef = database.getReference().child("userId").child(userId);
        userIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    DatabaseReference referralUserRef = FirebaseDatabase.getInstance().getReference().child("referralUser").child(userId);
                    referralUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long totalCount = dataSnapshot.getChildrenCount();
                            DatabaseReference myRef = database.getReference("users").child(userId);
                            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    //String point = snapshot.child("point").getValue().toString();
                                    String point = "0";
                                    if (snapshot.hasChild("point")) {
                                        point = snapshot.child("point").getValue().toString();
                                    }
                                    String referralPoint = "0";
                                    if (snapshot.hasChild("referralPoint")) {
                                        referralPoint = snapshot.child("referralPoint").getValue().toString();
                                    }

                                    double minusPoint = totalCount * Config.INSTANCE.getReferBonusReward();
                                    double pointDouble = Double.parseDouble(point);
                                    double updatedPoint =  pointDouble - minusPoint;
                                    if (updatedPoint <= 0) {
                                        updatedPoint = pointDouble;
                                    }

                                    double referralPointDouble = Double.parseDouble(referralPoint) + minusPoint;

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("point", String.valueOf(updatedPoint));
                                    hashMap.put("referralPoint", String.valueOf(referralPointDouble));
                                    myRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                userIdRef.setValue(true);
                                                SpManager.saveBoolean(SpManager.KEY_POINT_SEPARATED, true);
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle errors, if any
                            System.err.println("Error reading data: " + databaseError.getMessage());
                        }
                    });
                }
                else {
                    SpManager.saveBoolean(SpManager.KEY_POINT_SEPARATED, true);
                    Log.e("isPointSeparated", "isPointSeparated: true");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("isPointSeparated", "isPointSeparated: "+ isPointSeparated);
            }
        });
    }


}