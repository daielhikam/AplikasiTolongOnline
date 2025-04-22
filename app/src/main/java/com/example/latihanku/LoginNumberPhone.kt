package com.example.latihanku

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.latihanku.databinding.ActivityLoginNumberPhoneBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginNumberPhone : AppCompatActivity() {

    private lateinit var binding: ActivityLoginNumberPhoneBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginNumberPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = FirebaseAuth.getInstance()

        binding.btnSendOtp.setOnClickListener {
            val phoneNumber = "+62" + binding.etPhoneNumber.text.toString().trim()

            if (phoneNumber.isNotEmpty()) {
                sendOtp(phoneNumber)
            } else {
                Toast.makeText(this, "Masukkan nomor telepon yang valid", Toast.LENGTH_SHORT).show()
            }
        }
        binding.tvSignInHere.setOnClickListener {
            startActivity(Intent(this@LoginNumberPhone,LoginActivity::class.java))
        }
    }

    private fun sendOtp(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Auto-retrieval or instant validation
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(this@LoginNumberPhone, "OTP gagal dikirim: ${e.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    super.onCodeSent(verificationId, token)
                    // Simpan verificationId untuk verifikasi nanti
                    val intent = Intent(this@LoginNumberPhone, VerifyOtpActivity::class.java)
                    intent.putExtra("verificationId", verificationId)
                    startActivity(intent)
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    val user = task.result?.user
                    Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails
                    Toast.makeText(this, "Login Gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}