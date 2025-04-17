package com.example.latihanku


import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.latihanku.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var session: SessionManager
    private lateinit var callbackManager: CallbackManager
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            for (signature in info.signingInfo?.apkContentsSigners!!) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val keyHash = Base64.encodeToString(md.digest(), Base64.NO_WRAP)
                Log.d("KeyHash", "KeyHash: $keyHash")
            }
        } catch (e: Exception) {
            Log.e("KeyHash", "Error: ${e.message}")
        }

        auth = FirebaseAuth.getInstance()
        session = SessionManager(this)
        // Cek apakah sudah login
        if (auth.currentUser != null || session.isLoggedIn()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Google Login
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // From Firebase Console
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.btnGoogleLogin.setOnClickListener {
            googleSignInClient.signOut().addOnCompleteListener {
                googleSignInClient.revokeAccess().addOnCompleteListener {
                    val signInIntent = googleSignInClient.signInIntent
                    startActivityForResult(signInIntent, RC_SIGN_IN)
                }
            }
        }

        // Facebook Login
        callbackManager = CallbackManager.Factory.create()

        binding.btnFacebookLogin.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    handleFacebookAccessToken(result?.accessToken)
                }

                override fun onCancel() {}

                override fun onError(error: FacebookException) {
                    Toast.makeText(this@LoginActivity, "Facebook login failed", Toast.LENGTH_SHORT).show()
                }
            })
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }

        // Forgot password and register redirects
        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnBack.setOnClickListener {
            goToOnboarding()
        }
    }
    // Handle Google Sign-In
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data) // Handle Facebook result

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            session.setLoggedIn(true)
                            Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Login gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    session.setLoggedIn(true)
                    Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    val rawMessage = task.exception?.message ?: ""
                    Log.e("FIREBASE_ERROR", "Error: $rawMessage")

                    val errorMessage = when {
                        rawMessage.contains("auth credential is incorrect", ignoreCase = true) ->
                            "Email atau password salah"
                        rawMessage.contains("badly formatted", ignoreCase = true) ->
                            "Format email tidak valid"
                        rawMessage.contains("too many unsuccessful login attempts", ignoreCase = true) ->
                            "Terlalu banyak percobaan login, coba lagi nanti"
                        else ->
                            "Terjadi kesalahan saat login"
                    }

                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }
    // Handle Facebook Login
    private fun handleFacebookAccessToken(token: AccessToken?) {
        val credential = FacebookAuthProvider.getCredential(token?.token ?: "")
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    session.setLoggedIn(true)
                    Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, "Login gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Redirect to Onboarding screen
    private fun goToOnboarding() {
        val intent = Intent(this, Onboarding::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish() // Hapus LoginActivity dari back stack
    }
}