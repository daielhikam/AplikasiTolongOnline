package com.example.latihanku

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.LoggingBehavior

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inisialisasi Facebook SDK
        FacebookSdk.sdkInitialize(applicationContext)
        FacebookSdk.setIsDebugEnabled(true)
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS)
    }
}