package com.lotto.hanjang

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

class AdPrivacy(private val activity: Activity) {
    private val consent = UserMessagingPlatform.getConsentInformation(activity)
    private var initialized = false
    var canRequestAds by mutableStateOf(false)
        private set
    var optionsRequired by mutableStateOf(false)
        private set
    var busy by mutableStateOf(false)
        private set
    var message by mutableStateOf<String?>(null)
        private set

    fun refresh() {
        if (busy) return
        busy = true
        message = null
        consent.requestConsentInfoUpdate(activity, ConsentRequestParameters.Builder().build(), {
            if (activity.isFinishing || activity.isDestroyed) { busy = false; return@requestConsentInfoUpdate }
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { error ->
                busy = false
                message = error?.let { "광고 개인정보 설정을 확인하지 못했어요. 잠시 후 다시 시도해주세요." }
                update()
            }
        }, {
            busy = false
            message = "광고 개인정보 설정을 확인하지 못했어요. 잠시 후 다시 시도해주세요."
            update()
        })
    }

    fun showOptions() {
        if (busy || !optionsRequired) return
        busy = true
        canRequestAds = false
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { error ->
            busy = false
            message = error?.let { "광고 개인정보 설정을 열지 못했어요. 다시 시도해주세요." }
            update()
        }
    }

    private fun update() {
        optionsRequired = consent.privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
        canRequestAds = consent.canRequestAds()
        if (canRequestAds && !initialized) {
            initialized = true
            MobileAds.setRequestConfiguration(RequestConfiguration.Builder()
                .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G).build())
            MobileAds.initialize(activity) {}
        }
    }
}
