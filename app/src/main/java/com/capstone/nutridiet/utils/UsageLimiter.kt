package com.capstone.nutridiet.utils

import android.content.Context
import java.time.LocalDate

object UsageLimiter {

    private const val PREFS_NAME = "usage_limits"
    private const val MAX_USES_PER_DAY = 5

    // ─── KEYS ─────────────────────────────────────────
    private const val KEY_INSIGHT_COUNT = "insight_count"
    private const val KEY_INSIGHT_DATE  = "insight_date"
    private const val KEY_DIETPLAN_COUNT = "dietplan_count"
    private const val KEY_DIETPLAN_DATE  = "dietplan_date"

    // ─── CHECK IF CAN USE ─────────────────────────────
    fun canUseInsights(context: Context): Boolean {
        return getRemainingUses(context, KEY_INSIGHT_COUNT, KEY_INSIGHT_DATE) > 0
    }

    fun canUseDietPlan(context: Context): Boolean {
        return getRemainingUses(context, KEY_DIETPLAN_COUNT, KEY_DIETPLAN_DATE) > 0
    }

    // ─── REMAINING USES ───────────────────────────────
    fun getRemainingInsightUses(context: Context): Int {
        return getRemainingUses(context, KEY_INSIGHT_COUNT, KEY_INSIGHT_DATE)
    }

    fun getRemainingDietPlanUses(context: Context): Int {
        return getRemainingUses(context, KEY_DIETPLAN_COUNT, KEY_DIETPLAN_DATE)
    }

    // ─── RECORD USAGE ─────────────────────────────────
    fun recordInsightUse(context: Context) {
        recordUse(context, KEY_INSIGHT_COUNT, KEY_INSIGHT_DATE)
    }

    fun recordDietPlanUse(context: Context) {
        recordUse(context, KEY_DIETPLAN_COUNT, KEY_DIETPLAN_DATE)
    }

    // ─── PRIVATE HELPERS ──────────────────────────────
    private fun getRemainingUses(
        context: Context,
        countKey: String,
        dateKey: String
    ): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val today = LocalDate.now().toString()
        val savedDate = prefs.getString(dateKey, "")

        // ✅ reset count if it's a new day
        return if (savedDate != today) {
            MAX_USES_PER_DAY
        } else {
            val used = prefs.getInt(countKey, 0)
            (MAX_USES_PER_DAY - used).coerceAtLeast(0)
        }
    }
    fun refundInsightUse(context: Context) {
        refundUse(context, KEY_INSIGHT_COUNT, KEY_INSIGHT_DATE)
    }

    fun refundDietPlanUse(context: Context) {
        refundUse(context, KEY_DIETPLAN_COUNT, KEY_DIETPLAN_DATE)
    }

    private fun refundUse(
        context: Context,
        countKey: String,
        dateKey: String
    ) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val today = LocalDate.now().toString()
        val savedDate = prefs.getString(dateKey, "")

        if (savedDate == today) {
            val currentCount = prefs.getInt(countKey, 0)
            if (currentCount > 0) {
                prefs.edit()
                    .putInt(countKey, currentCount - 1)
                    .apply()
            }
        }
    }
    private fun recordUse(
        context: Context,
        countKey: String,
        dateKey: String
    ) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val today = LocalDate.now().toString()
        val savedDate = prefs.getString(dateKey, "")

        val currentCount = if (savedDate != today) {
            0  // reset on new day
        } else {
            prefs.getInt(countKey, 0)
        }

        prefs.edit()
            .putInt(countKey, currentCount + 1)
            .putString(dateKey, today)
            .apply()
    }
}