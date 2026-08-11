package com.capstone.nutridiet.network

import android.content.Context
import android.util.Log
import com.capstone.nutridiet.database.BloodPressureLog
import com.capstone.nutridiet.utils.AnonymousUserManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalDateTime

class FirestoreRepository(private val context: Context) {

    private val db = FirebaseFirestore.getInstance()
    private val userId = AnonymousUserManager.getUserId(context)

    // ─── UPLOAD BP READING ────────────────────────────
    suspend fun uploadBloodPressureReading(
        systolic: Int,
        diastolic: Int,
        hypertensionLevel: String
    ) {
        try {
            val data = hashMapOf(
                "userId"            to userId,
                "systolic"          to systolic,
                "diastolic"         to diastolic,
                "hypertensionLevel" to hypertensionLevel,
                "date"              to LocalDate.now().toString(),
                "timestamp"         to com.google.firebase.Timestamp.now()
            )

            db.collection("blood_pressure_logs")
                .add(data)
                .await()

            Log.d("Firestore", "BP reading uploaded for $userId")

        } catch (e: Exception) {
            // ✅ silent fail — don't interrupt user experience
            Log.e("Firestore", "Failed to upload BP: ${e.message}")
        }
    }

    // ─── UPLOAD USER PROFILE (anonymous) ──────────────
    suspend fun uploadUserProfile(
        age: Int,
        isMale: Boolean,
        hypertensionLevel: String,
        calorieGoal: Int,
        activityLevel: String
    ) {
        try {
            val data = hashMapOf(
                "userId"            to userId,
                "age"               to age,
                "gender"            to if (isMale) "Male" else "Female",
                "hypertensionLevel" to hypertensionLevel,
                "calorieGoal"       to calorieGoal,
                "activityLevel"     to activityLevel,
                "lastUpdated"       to com.google.firebase.Timestamp.now()
            )

            // ✅ upsert — creates or updates
            db.collection("user_profiles")
                .document(userId)
                .set(data, SetOptions.merge())
                .await()

            Log.d("Firestore", "Profile uploaded for $userId")

        } catch (e: Exception) {
            Log.e("Firestore", "Failed to upload profile: ${e.message}")
        }
    }

    // ─── GET USER ID ──────────────────────────────────
    fun getAnonymousUserId(): String = userId
}