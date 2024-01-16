package com.orbaic.miner;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdLoadCallback;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.ResponseInfo;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.orbaic.miner.quiz.AdmobDataChange;

public class AdMobAds {
    private Context context;
    private AdmobDataChange dataChange;

    private AdLoadCallback listener;
    private InterstitialAd admobInterstitialAd;
    private RewardedInterstitialAd rewardedInterstitialAd;
    private String button = "";
     private Activity getActivity;
    public AdMobAds(Context context, Activity getActivity){
        this.context = context;
        this.getActivity = getActivity;
    }

    public void setAdLoadCallback(AdLoadCallback listener) {
        this.listener = listener;
    }

    public void loadRewardInterstitial(){
        if (rewardedInterstitialAd == null) {
            RewardedInterstitialAd.load(context, "ca-app-pub-9323045181924630/9843345511",
                    new AdRequest.Builder().build(),  new RewardedInterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(RewardedInterstitialAd ad) {
                            ResponseInfo responseInfo = ad.getResponseInfo();
                            System.out.printf(responseInfo.toString());

                            button = "ON";
                            rewardedInterstitialAd = ad;
                            System.out.println(ad);
                        }
                        @Override
                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                            ResponseInfo responseInfo = loadAdError.getResponseInfo();
                            System.out.printf(responseInfo.toString());
                            rewardedInterstitialAd = null;
                            loadRewardInterstitial();
                        }
                    });
        }
    }

    public void showRewardInterstitialAds(){
        if (rewardedInterstitialAd != null){
            rewardedInterstitialAd.show(getActivity, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    System.out.println("Successfully done");
                }
            });

            rewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    rewardedInterstitialAd = null;
                    loadRewardInterstitial();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);

                    System.out.println(adError.getMessage());

                    rewardedInterstitialAd = null;
                    loadRewardInterstitial();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                }
            });
        }
    }

    public void loadIntersAndRewardedAd() {
        if (admobInterstitialAd != null) {
            admobInterstitialAd = null;
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
                            admobInterstitialAd = ad;
                            button = "ON";
                            //Log.i(TAG, "onAdLoaded");
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            ResponseInfo responseInfo = loadAdError.getResponseInfo();
                            // Handle the error
                            admobInterstitialAd = null;
                            loadIntersAndRewardedAd();
                            System.out.printf(String.valueOf(responseInfo));
                        }
                    });
        }else{
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
                            admobInterstitialAd = ad;
                            button = "ON";
                            //Log.i(TAG, "onAdLoaded");
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            ResponseInfo responseInfo = loadAdError.getResponseInfo();
                            // Handle the error
                            admobInterstitialAd = null;
                            loadIntersAndRewardedAd();
                            System.out.printf(String.valueOf(responseInfo));
                        }
                    });
        }
    }

    public void showRewardedVideo() {

        if (admobInterstitialAd == null) {
            //Log.d("TAG", "The rewarded ad wasn't ready yet.");
            //Toast.makeText(context, "ads not work", Toast.LENGTH_SHORT).show();
            loadIntersAndRewardedAd();
            return;
        }else{
            //rewardedInterstitialAd.show(getActivity);
            admobInterstitialAd.show(getActivity);
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
                        loadIntersAndRewardedAd();
                    }

                    /** Called when full screen content is dismissed. */
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        admobInterstitialAd = null;
                        // Preload the next rewarded interstitial ad.
                        loadIntersAndRewardedAd();
                    }

                });

    }

    public String getButton() {
        return button;
    }
}
