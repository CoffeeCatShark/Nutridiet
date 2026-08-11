package com.capstone.nutridiet.utils

import android.content.Context
import java.util.UUID

object AnonymousUserManager {

    private const val PREFS_NAME = "nutridiet_user"
    private const val KEY_USER_ID = "anonymous_user_id"
    private const val KEY_CONSENT_GIVEN = "consent_given"

    fun getUserId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_USER_ID, null)

        return if (existing != null) {
            existing
        } else {
            // ✅ use Firestore's auto-generated document ID
            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            val newId = db.collection("user_profiles").document().id
            // example: "xK9mP2qR4nL8vW3j"
            prefs.edit().putString(KEY_USER_ID, newId).apply()
            newId
        }
    }

    fun hasGivenConsent(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_CONSENT_GIVEN, false)
    }

    fun saveConsent(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_CONSENT_GIVEN, true).apply()
    }
}