package com.capstone.nutridiet

object FoodScoreUtils {

    fun getFoodScoreRating(foodScore: Double): String {
        return when {
            foodScore < 50.0 -> "Bad"
            foodScore < 69.0 -> "Decent"
            foodScore > 69.0 -> "Good"
            else -> "ERROR"
        }
    }

    enum class FoodScore(val displayName: String, val description: String, val rating: Char){
        GOOD("Good", "Good Description", 'A'),
        DECENT("Decent", "Decent Description", 'B'),
        BAD("Bad", "Bad Description", 'C')



    }


}