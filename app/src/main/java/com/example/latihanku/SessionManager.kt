package com.example.latihanku


import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    // Menyimpan status login
    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean("isLoggedIn", isLoggedIn).apply()
    }

    // Mengecek status login
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("isLoggedIn", false)
    }

    // Menghapus session (logout)
    fun clearSession() {
        prefs.edit().clear().apply() // Menghapus semua data session
    }
}