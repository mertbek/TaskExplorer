package com.mertbek.taskexplorer.data.local

import android.content.Context
import android.content.SharedPreferences
import com.mertbek.taskexplorer.R

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("task_explorer_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_USER_NAME = "user_name"
        const val KEY_USER_ID = "user_id"
        const val KEY_USER_UNIT = "user_unit"
        const val KEY_LANGUAGE = "language_code"
    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(KEY_ACCESS_TOKEN, token)
        editor.apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun clearSession() {
        prefs.edit().apply {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_USER_NAME)
            remove(KEY_USER_ID)
            remove(KEY_USER_UNIT)
            apply()
        }
    }

    fun saveUserInfo(name: String, personalNo: String, unit: String) {
        prefs.edit().apply {
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_ID, personalNo)
            putString(KEY_USER_UNIT, unit)
            apply()
        }
    }

    fun fetchUserInfo(): Map<String, String> {
        return mapOf(
            "name" to (prefs.getString(KEY_USER_NAME, R.string.user_default.toString()) ?: R.string.user_default.toString()),
            "id" to (prefs.getString(KEY_USER_ID, "") ?: ""),
            "unit" to (prefs.getString(KEY_USER_UNIT, "") ?: "")
        )
    }

    fun saveLanguage(languageCode: String) {
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    fun fetchLanguage(): String {
        return prefs.getString(KEY_LANGUAGE, "en") ?: "en"
    }
}