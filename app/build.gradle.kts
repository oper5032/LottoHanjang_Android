plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}
android {
    namespace = "com.lotto.hanjang"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.lotto.hanjang"
        minSdk = 26
        targetSdk = 36
        versionCode = 3
        versionName = "1.0.2"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    buildTypes {
        getByName("debug") {
            manifestPlaceholders["admobAppId"] = "ca-app-pub-3940256099942544~3347511713"
            buildConfigField("String", "REWARDED_AD_UNIT_ID", "\"ca-app-pub-3940256099942544/5224354917\"")
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            manifestPlaceholders["admobAppId"] = "ca-app-pub-4975576972120488~2840133283"
            buildConfigField("String", "REWARDED_AD_UNIT_ID", "\"ca-app-pub-4975576972120488/3836238925\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
dependencies {
    // Override the obsolete Fragment 1.1.0 pulled in by Google Play services.
    implementation("androidx.fragment:fragment:1.5.4")
    implementation(platform("androidx.compose:compose-bom:2025.01.00"))
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.datastore:datastore-preferences:1.1.2")
    implementation("com.google.android.gms:play-services-ads:23.6.0")
    implementation("com.google.android.ump:user-messaging-platform:4.0.0")
    testImplementation("junit:junit:4.13.2")
}
