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

     implementation(libs.play.services.auth)
    implementation(libs.firebase.ui.auth)

    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.firebase.auth)

    implementation(libs.circleimageview)

    //foto
    implementation(libs.glide)
    implementation(libs.play.services.maps)
    kapt(libs.compiler)


    // OSMDroid untuk peta
    implementation("org.osmdroid:osmdroid-android:6.1.10")
    //google play service
    implementation("com.google.android.gms:play-services-location:21.3.0")
    // Untuk OkHttpClient
    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0") // Untuk response string
    implementation("com.android.volley:volley:1.2.1")

    // untuk request ke OpenRouteService
    implementation(libs.okhttp)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

