package com.example.taskmanager.utils

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("TaskManagerPrefs", Context.MODE_PRIVATE)

    companion object{
        private const val  KEY_TOKEN = "jwt_token"
    }

    fun saveToken(token: String){
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun clearToken(){
        prefs.edit().remove(KEY_TOKEN).apply()
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}
