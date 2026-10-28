package com.lotto.hanjang

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object RewardAds {
    // Google's dedicated Android rewarded test unit. Never requests production ads.
    private const val TEST_UNIT = "ca-app-pub-3940256099942544/5224354917"
    fun show(activity: Activity, earned: () -> Unit, closed: (Boolean) -> Unit, failed: () -> Unit) {
        RewardedAd.load(activity, TEST_UNIT, AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(error: LoadAdError) = failed()
            override fun onAdLoaded(ad: RewardedAd) {
                if (activity.isFinishing || activity.isDestroyed) { failed(); return }
                var rewarded = false
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() = closed(rewarded)
                    override fun onAdFailedToShowFullScreenContent(error: AdError) = failed()
                }
                ad.show(activity) {
                    if (!rewarded) { rewarded = true; earned() }
                }
            }
        })
    }
}
