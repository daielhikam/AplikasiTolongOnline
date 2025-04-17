plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-kapt")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)


}

android {

    viewBinding {
        enable = true
    }

    namespace = "com.example.latihanku"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.latihanku"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        viewBinding {
            enable = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.androidx.ui)
    implementation(libs.androidx.material)

     implementation(libs.play.services.auth)
    implementation(libs.firebase.ui.auth)
    implementation(libs.facebook.android.sdk)

    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.firebase.auth)

    implementation(libs.circleimageview)

    //foto
    implementation(libs.glide)
    implementation(libs.play.services.maps)
    kapt(libs.compiler)

    //maps
    implementation(libs.osmdroid.android)

    // untuk request ke OpenRouteService
    implementation(libs.okhttp)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

