package com.capstone.nutridiet.utils

import android.content.Context

object AnonymousUserManager {

    private const val PREFS_NAME = "nutridiet_user"
    private const val KEY_USER_ID = "anonymous_user_id"
    private const val KEY_CONSENT_GIVEN = "consent_given"

    // ✅ generate or retrieve anonymous UID
    fun getUserId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_USER_ID, null)

        return if (existing != null) {
            existing
        } else {
            // generate new UID like UID0001
            val newId = generateUID(context)
            prefs.edit().putString(KEY_USER_ID, newId).apply()
            newId
        }
    }

    private fun generateUID(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val count = prefs.getInt("uid_counter", 1)
        prefs.edit().putInt("uid_counter", count + 1).apply()
        return "UID${count.toString().padStart(4, '0')}"
    }

    // ✅ check if user has given consent
    fun hasGivenConsent(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_CONSENT_GIVEN, false)
    }

    // ✅ save consent
    fun saveConsent(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_CONSENT_GIVEN, true).apply()
    }
}