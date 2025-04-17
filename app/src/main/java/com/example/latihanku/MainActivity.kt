package com.example.latihanku

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.latihanku.databinding.ActivityMainBinding
import com.example.latihanku.fragmen.ChatFragment
import com.example.latihanku.fragmen.HistoryFragment
import com.example.latihanku.fragmen.HomeFragment
import com.example.latihanku.fragmen.ProfileFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val headerView = binding.navigationView.getHeaderView(0)
        val tvName = headerView.findViewById<TextView>(R.id.tvName)
        val tvPhone = headerView.findViewById<TextView>(R.id.tvPhone)
        val account = GoogleSignIn.getLastSignedInAccount(this)
        val photoUrl = account?.photoUrl

        val imageView = headerView.findViewById<ImageView>(R.id.imgProfile)

        if (photoUrl != null) {
            Glide.with(this)
                .load(photoUrl)
                .into(imageView)
        } else {
            Glide.with(this)
                .load(R.drawable.profile_kosong)
                .into(imageView)
        }
        tvName.text = user?.displayName ?: "Nama tidak tersedia"
        tvPhone.text = user?.email ?: "Email tidak tersedia"

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .commit()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_history -> replaceFragment(HistoryFragment())
                R.id.nav_chat -> replaceFragment(ChatFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
