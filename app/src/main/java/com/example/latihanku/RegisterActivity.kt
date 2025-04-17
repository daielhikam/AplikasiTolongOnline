package com.example.latihanku

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.latihanku.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvLogin.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        // Inisialisasi FirebaseAuth
        auth = FirebaseAuth.getInstance()

        binding.btnCreateAccount.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(email, password)
            }
        }
    }

    private fun registerUser(email: String, password: String) {
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Pendaftaran berhasil", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val rawMessage = task.exception?.message
                    val errorMessage = when {
                        rawMessage?.contains("email address is already in use") == true -> "Email sudah digunakan"
                        rawMessage?.contains("badly formatted") == true -> "Format email tidak valid"
                        rawMessage?.contains("Password should be at least") == true -> "Password minimal 6 karakter"
                        else -> "Pendaftaran gagal: $rawMessage"
                    }

                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }
}
