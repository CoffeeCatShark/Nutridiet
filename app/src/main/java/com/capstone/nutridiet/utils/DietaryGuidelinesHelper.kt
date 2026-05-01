package com.capstone.nutridiet.utils

object DietaryGuidelinesHelper {

    // ─── DIETARY CONSTRAINTS PER LEVEL ────────────────
    fun getDietaryConstraints(level: HypertensionLevel): String {
        return when (level) {
            HypertensionLevel.NORMAL -> """
                - Maintain a balanced diet
                - Sodium limit: under 2300mg/day (AHA 2017)
                - Focus on whole foods and lean proteins
                - Maintain healthy weight
                - DASH diet principles encouraged
            """.trimIndent()

            HypertensionLevel.ELEVATED -> """
                - Begin DASH diet principles (Appel et al. 1997)
                - Sodium limit: under 2300mg/day
                - Reduce processed and high-sodium foods
                - Increase fruits, vegetables, whole grains
                - Reduce saturated fat intake below 6% of calories
            """.trimIndent()

            HypertensionLevel.STAGE_1 -> """
                - Strict DASH diet required (Sacks et al. 2001)
                - Sodium limit: under 1500mg/day
                - Strictly avoid high sodium foods
                - Prioritize potassium-rich foods (WHO 2012)
                - Limit saturated fat to under 6% of calories
                - Avoid red meat and processed foods
                - Regular blood pressure monitoring recommended
            """.trimIndent()

            HypertensionLevel.STAGE_2 -> """
                - Strict DASH diet with medical supervision
                - Sodium limit: under 1500mg/day
                - Eliminate all high sodium foods
                - Maximize potassium, magnesium, calcium intake
                - Saturated fat limit under 5% of calories
                - Only lean proteins allowed
                - No processed or fried foods
                - Medical consultation strongly recommended
            """.trimIndent()

            HypertensionLevel.CRISIS -> """
                - EMERGENCY: Seek immediate medical attention
                - Strictest sodium restriction: under 1000mg/day
                - Only very low sodium foods
                - No saturated fat whatsoever
                - Only the healthiest foods from the list
                - Diet alone CANNOT treat hypertensive crisis
                - Call emergency services immediately
            """.trimIndent()
        }
    }

    // ─── FOODS TO PRIORITIZE PER LEVEL ────────────────
    fun getFoodPriorities(level: HypertensionLevel): String {
        return when (level) {
            HypertensionLevel.NORMAL -> """
                Prioritize: fruits, vegetables, whole grains,
                lean proteins, low-fat dairy
            """.trimIndent()

            HypertensionLevel.ELEVATED -> """
                Prioritize: potassium-rich foods (banana, camote),
                low-sodium proteins (bangus, tilapia),
                high-fiber vegetables (kangkong, ampalaya)
            """.trimIndent()

            HypertensionLevel.STAGE_1,
            HypertensionLevel.STAGE_2 -> """
                Prioritize: very low sodium foods (<200mg per serving),
                high potassium (banana, malunggay, kamote),
                lean fish (bangus, tilapia, galunggong),
                legumes (monggo, tokwa),
                fresh vegetables (kangkong, pechay, sitaw)
            """.trimIndent()

            HypertensionLevel.CRISIS -> """
                Only very low sodium foods (<100mg per serving).
                Fresh fruits and vegetables only.
                No added salt whatsoever.
                Seek emergency care — diet is secondary.
            """.trimIndent()
        }
    }

    // ─── FOODS TO AVOID PER LEVEL ─────────────────────
    fun getFoodAvoidances(level: HypertensionLevel): String {
        return when (level) {
            HypertensionLevel.NORMAL -> """
                Limit: processed foods, excessive sodium,
                sugary drinks, excessive red meat
            """.trimIndent()

            HypertensionLevel.ELEVATED -> """
                Avoid: processed meats, canned goods,
                fast food, salty snacks, full-fat dairy
            """.trimIndent()

            HypertensionLevel.STAGE_1,
            HypertensionLevel.STAGE_2 -> """
                Strictly avoid: dilis (dried anchovies),
                processed meats, canned sardines in brine,
                lechon kawali, salty chips and crackers,
                instant noodles, bagoong, patis, toyo
            """.trimIndent()

            HypertensionLevel.CRISIS -> """
                Avoid ALL high sodium foods immediately.
                No processed, canned, or preserved foods.
                No added condiments (bagoong, patis, toyo).
                Emergency medical care is the priority.
            """.trimIndent()
        }
    }

    // ─── SODIUM LIMIT TEXT ────────────────────────────
    fun getSodiumLimitText(level: HypertensionLevel): String {
        val limit = BloodPressureAnalyzer.getSodiumLimit(level)
        return "$limit mg/day (${getSourceCitation(level)})"
    }

    // ─── CITATIONS ────────────────────────────────────
    private fun getSourceCitation(level: HypertensionLevel): String {
        return when (level) {
            HypertensionLevel.NORMAL,
            HypertensionLevel.ELEVATED ->
                "AHA 2017, WHO 2012"
            HypertensionLevel.STAGE_1,
            HypertensionLevel.STAGE_2 ->
                "DASH-Sodium Trial, Sacks et al. NEJM 2001"
            HypertensionLevel.CRISIS ->
                "AHA Emergency Guidelines"
        }
    }
}