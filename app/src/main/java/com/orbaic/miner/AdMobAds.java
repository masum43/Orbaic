package com.orbaic.miner;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.ResponseInfo;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

public class AdMobAds {
    private Context context;
    private InterstitialAd interstitialAd;
    private RewardedInterstitialAd rewardedInterstitialAd;
    private String button = "";
     private Activity getActivity;
    public AdMobAds(Context context, Activity getActivity){
        this.context = context;
        this.getActivity = getActivity;
    }

    public void loadRewardInterstitial(){
        if (rewardedInterstitialAd == null){
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

    public void loadRewardedAd() {

        if (interstitialAd == null) {

            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(context,"ca-app-pub-9323045181924630/7438005611", adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd ad) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            interstitialAd = ad;
                            button = "ON";
                            //Log.i(TAG, "onAdLoaded");
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            ResponseInfo responseInfo = loadAdError.getResponseInfo();
                            // Handle the error
                            interstitialAd = null;
                            loadRewardedAd();
                            System.out.printf(String.valueOf(responseInfo));
                        }
                    });


        }
    }

    public void showRewardedVideo() {

        if (interstitialAd == null) {
            //Log.d("TAG", "The rewarded ad wasn't ready yet.");
            //Toast.makeText(context, "ads not work", Toast.LENGTH_SHORT).show();
            loadRewardedAd();
            return;
        }else{
            //rewardedInterstitialAd.show(getActivity);
            interstitialAd.show(getActivity);
        }

        interstitialAd.setFullScreenContentCallback(
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
                        interstitialAd = null;
                        loadRewardedAd();
                    }

                    /** Called when full screen content is dismissed. */
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        interstitialAd = null;
                        // Preload the next rewarded interstitial ad.
                        loadRewardedAd();
                    }

                });

    }

    public String getButton() {
        return button;
    }
}
