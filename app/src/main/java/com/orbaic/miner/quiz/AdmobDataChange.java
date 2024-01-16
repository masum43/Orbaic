package com.orbaic.miner.quiz;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.ResponseInfo;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class AdmobDataChange extends ViewModel {

    private InterstitialAd admobInterstitialAd;
    private Context context;
    private Activity activity;
    MutableLiveData<String> admobStatus = new MutableLiveData<>();

    public LiveData<String> getAdmobStatus() {
        return admobStatus;
    }



    public void needData(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }


    public void loadAds() {
        if (admobInterstitialAd == null) {
            AdRequest adRequest = new AdRequest.Builder().build();
            String intersId = "ca-app-pub-9323045181924630/7438005611";
            if (com.orbaic.miner.BuildConfig.DEBUG) {
                intersId = "ca-app-pub-3940256099942544/1033173712";
            }
            InterstitialAd.load(context,intersId, adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd ad) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            admobStatus.setValue("on");
                            admobInterstitialAd = ad;
                            //Log.i(TAG, "onAdLoaded");
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            ResponseInfo responseInfo = loadAdError.getResponseInfo();
                            // Handle the error
                            admobInterstitialAd = null;
                            System.out.printf(String.valueOf(responseInfo));
                            admobStatus.setValue("off");
                            loadAds();
                        }
                    });

        }
    }

    public void showAds() {
        if (admobInterstitialAd != null) {
            admobInterstitialAd.show(activity);
        }else {
            admobInterstitialAd = null;
            loadAds();
            return;
        }

        admobInterstitialAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    /** Called when ad showed the full screen content. */
                    @Override
                    public void onAdShowedFullScreenContent() {

                    }

                    /** Called when the ad failed to show full screen content. */
                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        System.out.printf(adError.getMessage());

                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        admobInterstitialAd = null;
                        admobStatus.setValue("off");
                        Toast.makeText(context, "ad loading", Toast.LENGTH_SHORT).show();
                        loadAds();
                    }

                    /** Called when full screen content is dismissed. */
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        admobInterstitialAd = null;
                        // Preload the next rewarded interstitial ad.
                        admobStatus.setValue("off");
                        loadAds();
                    }

                });
    }
}
