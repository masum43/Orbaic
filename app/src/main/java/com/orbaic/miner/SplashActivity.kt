package com.orbaic.miner

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.orbaic.miner.auth.LoginLayout
import com.orbaic.miner.common.CustomDialog
import com.orbaic.miner.common.SpManager
import java.util.Timer
import java.util.TimerTask

class SplashActivity : AppCompatActivity() {
    var timer = Timer()
    var versionCode = 0
    var progressBar: ProgressBar? = null
    private var requestPermissionLauncher: ActivityResultLauncher<String>? = null
    private var mobAds: AdMobAds? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mobAds = AdMobAds(this, this)
        MobileAds.initialize(this)
        mobAds!!.loadIntersAndRewardedAd()
        progressBar = findViewById(R.id.progressBar)
        SpManager.saveBoolean(SpManager.KEY_IS_TAP_TARGET_SHOW, true)

        // Initialize ActivityResultLauncher for requesting permissions
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean? -> dataRead() }
        if (isNotificationPermissionGranted) {
            dataRead()
        } else {
            askNotificationPermission()
        }
    }

    private fun updateNotice() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Update Notification")
        builder.setMessage("We update our app. Please update the app from Play Store")
        builder.setCancelable(false)
        builder.setPositiveButton("Update") { dialogInterface: DialogInterface?, i: Int ->
            val appPackageName = packageName
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW, Uri.parse(
                            "market://details?id=$appPackageName"
                        )
                    )
                )
            } catch (anfe: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW, Uri.parse(
                            "https://play.google.com/store/apps/details?id=$appPackageName"
                        )
                    )
                )
            }
        }
        builder.create().show()
    }

    private fun dataRead() {
        if (!isNetworkConnected) {
            internetConnectionLost()
            return
        }
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val reference = firebaseDatabase.getReference("app")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val code = snapshot.child("appVersion").value.toString()
                val isShowNotice = snapshot.child("isShowNotice").getValue(
                    Boolean::class.java
                )!!
                val isNoticeCancelable = snapshot.child("isNoticeCancelable").getValue(
                    Boolean::class.java
                )!!
                val notice = snapshot.child("notice").getValue(
                    String::class.java
                )
                versionCode = code.toInt()
                if (isShowNotice) {
                    showMaintenanceDialog(notice!!, isNoticeCancelable)
                } else {
                    startTimerTask()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                if (error.code == DatabaseError.NETWORK_ERROR || error.code == DatabaseError.DISCONNECTED) {
                    // Show dialog for no internet or network issue
                    internetConnectionLost()
                } else {
                    Toast.makeText(this@SplashActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun showMaintenanceDialog(notice: String, isCancelable: Boolean) {
        val customDialog = CustomDialog(this)
        customDialog.showMaintenanceDialog(notice, onClick = {
            if (isCancelable) {
                startTimerTask()
            }
            else {
                finishAffinity()
            }
        });
    }

    private fun startTimerTask() {
        val timerTask1: TimerTask = object : TimerTask() {
            override fun run() {
                runOnUiThread(object : Runnable {
                    override fun run() {
                        if (isNetworkConnected) {
                            val appVersionCode = BuildConfig.VERSION_CODE
                            if (versionCode > appVersionCode) {
                                updateNotice()
                                progressBar!!.visibility = View.GONE
                                return
                            } else {
                                progressBar!!.visibility = View.GONE
                                navigateToNextActivity()
                            }
                        } else {
                            Toast.makeText(
                                this@SplashActivity,
                                "Please Check Internet Connection",
                                Toast.LENGTH_LONG
                            ).show()
                            internetConnectionLost()
                            progressBar!!.visibility = View.GONE
                        }
                    }
                })
            }
        }
        timer.schedule(timerTask1, 2000)
    }

    private fun navigateToNextActivity() {
        val intent = Intent(this@SplashActivity, LoginLayout::class.java)
        startActivity(intent)
        finish()
    }

    private fun internetConnectionLost() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("No Internet")
        builder.setMessage("No Internet connection. Please check your Internet")
        builder.setCancelable(false)
        builder.setPositiveButton("Exit") { dialogInterface, i -> finishAffinity() }
        builder.create().show()
    }

    private val isNetworkConnected: Boolean
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

    }*/private get() {
            val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                val activeNetwork = connectivityManager.activeNetworkInfo
                return activeNetwork != null && activeNetwork.isConnected
            }
            return false
        }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher!!.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private val isNotificationPermissionGranted: Boolean
        private get() = if (Build.VERSION.SDK_INT >= 33) {
            // For Android 13 (or later), check the notification permission
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            // For versions older than Android 13, assume permission is granted
            true
        }
}