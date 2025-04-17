package com.example.latihanku

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.latihanku.databinding.ActivityLoginNumberPhoneBinding
import com.google.firebase.auth.FirebaseAuth

class LoginNumberPhone : AppCompatActivity() {
    private lateinit var binding: ActivityLoginNumberPhoneBinding
    private lateinit var session: SessionManager
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginNumberPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        // Cek apakah sudah login
        if (auth.currentUser != null || session.isLoggedIn()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnLogin.setOnClickListener {

        }
        binding.tvSignInHere.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}