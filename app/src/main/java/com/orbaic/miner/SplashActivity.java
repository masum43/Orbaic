package com.orbaic.miner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orbaic.miner.auth.LoginLayout;
import com.orbaic.miner.common.SpManager;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {


    Timer timer = new Timer();

    int versionCode;
    ProgressBar progressBar;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);

        SpManager.saveBoolean(SpManager.KEY_IS_TAP_TARGET_SHOW, true);

        // Initialize ActivityResultLauncher for requesting permissions
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    dataRead();
                }
        );

        if (isNotificationPermissionGranted()) {
            dataRead();
        }
        else {
            askNotificationPermission();
        }






    }

    private void updateNotice() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Notification");
        builder.setMessage("We update our app. Please update the app from Play Store");
        builder.setCancelable(false);
        builder.setPositiveButton("Update", (dialogInterface, i) -> {
            final String appPackageName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        });
        builder.create().show();
    }

    private void dataRead() {
        if (!isNetworkConnected()){
            internetConnectionLost();
            return;
        }

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference("app");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String code = snapshot.child("appVersion").getValue().toString();
                System.out.println(code);
                versionCode = Integer.parseInt(code);

                startTimerTask();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (error.getCode() == DatabaseError.NETWORK_ERROR || error.getCode() == DatabaseError.DISCONNECTED) {
                    // Show dialog for no internet or network issue
                    internetConnectionLost();
                }
                else
                {
                    Toast.makeText(SplashActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    private void startTimerTask() {
        TimerTask timerTask1 = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isNetworkConnected()) {
                            int appVersionCode = BuildConfig.VERSION_CODE;
                            if( versionCode > appVersionCode) {
                                updateNotice();
                                progressBar.setVisibility(View.GONE);
                                return;
                            }else {
                                progressBar.setVisibility(View.GONE);
                                navigateToNextActivity();
                            }
                        }else {
                            Toast.makeText(SplashActivity.this,"Please Check Internet Connection", Toast.LENGTH_LONG).show();
                            internetConnectionLost();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask1,2000);
    }

    private void navigateToNextActivity() {
        Intent intent = new Intent(SplashActivity.this, LoginLayout.class);
        startActivity(intent);
        finish();
    }

    private void internetConnectionLost() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet");
        builder.setMessage("No Internet connection. Please check your Internet");
        builder.setCancelable(false);
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
            }
        });
        builder.create().show();
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

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private boolean isNotificationPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 33) {
            // For Android 13 (or later), check the notification permission
            return ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED;
        } else {
            // For versions older than Android 13, assume permission is granted
            return true;
        }
    }
}