package com.example.latihanku.fragmen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.latihanku.MainActivity
import com.example.latihanku.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Delay selama 2 detik, lalu pindah ke MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 2000) // 2000 ms = 2 detik
    }
}
