package com.example.latihanku
import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean("isLoggedIn", isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("isLoggedIn", false)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}