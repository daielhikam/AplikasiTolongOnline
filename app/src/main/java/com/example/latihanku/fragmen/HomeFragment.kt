package com.example.latihanku.fragmen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.latihanku.LoginActivity
import com.example.latihanku.MapsActivity
import com.example.latihanku.R
import com.example.latihanku.SessionManager
import com.example.latihanku.databinding.FragmentHomeBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var session: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        session = SessionManager(requireContext())

        val drawerLayout = binding.drawerLayout
        val navigationView = binding.navigationView
        val btnDrawer = binding.btnDrawer

        btnDrawer.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val headerView = navigationView.getHeaderView(0)
        val tvName = headerView.findViewById<TextView>(R.id.tvName)
        val tvEmail = headerView.findViewById<TextView>(R.id.tvPhone)
        val imageView = headerView.findViewById<ImageView>(R.id.imgProfile)

        val user = auth.currentUser
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        val photoUrl = account?.photoUrl

        if (photoUrl != null) {
            Glide.with(this).load(photoUrl).into(imageView)
        } else {
            Glide.with(this).load(R.drawable.profile_kosong).into(imageView)
        }

        tvName.text = user?.displayName ?: "Nama tidak tersedia"
        tvEmail.text = user?.email ?: "Email tidak tersedia"

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_profile -> showToast("My Profile")
                R.id.nav_settings -> showToast("Settings")
                R.id.nav_help -> showToast("Need Help")
                R.id.nav_chat -> showToast("Chat Rescue")
                R.id.nav_logout -> showLogoutDialog()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        binding.imgMaps.setOnClickListener {
            val intent = Intent(requireContext(), MapsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Apakah kamu yakin ingin logout?")
            .setPositiveButton("Ya") { _, _ ->
                auth.signOut()
                session.clearSession()

                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}