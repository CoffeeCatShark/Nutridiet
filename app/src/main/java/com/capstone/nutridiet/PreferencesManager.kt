package com.capstone.nutridiet.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.capstone.nutridiet.utils.PreferencesManager.Companion.HAS_GIVEN_CONSENT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nutridiet_prefs")

class PreferencesManager(private val context: Context) {

    // ✅ only ONE companion object with ALL keys
    companion object {
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        val HAS_GIVEN_CONSENT = booleanPreferencesKey("has_given_consent")
    }

    // ─── FIRST LAUNCH ─────────────────────────────────
    val isFirstLaunch: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_FIRST_LAUNCH] ?: true
        }

    suspend fun setFirstLaunchComplete() {
        context.dataStore.edit { preferences ->
            preferences[IS_FIRST_LAUNCH] = false
        }
    }

    // ─── CONSENT ──────────────────────────────────────
    val hasGivenConsent: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[HAS_GIVEN_CONSENT] ?: false
        }

    suspend fun saveConsent(given: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[HAS_GIVEN_CONSENT] = given
        }
    }
}