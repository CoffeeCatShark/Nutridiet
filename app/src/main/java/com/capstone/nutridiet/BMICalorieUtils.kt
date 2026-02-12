package com.capstone.nutridiet

import com.capstone.nutridiet.BMICalorieUtils.ActivityLevel.SEDENTARY

object BMICalorieUtils {



    /**
     * Calculate BMI status category based on BMI value
     */
    fun getBMIStatus(bmi: Float): String {
        return when {
            bmi < 18.5f -> "Underweight"
            bmi < 25f -> "Normal"
            bmi < 30f -> "Overweight"
            else -> "Obese"
        }
    }

    /**
     * Calculate BMI status category based on BMI value (Double version)
     */
    fun getBMIStatus(bmi: Double): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi < 25.0 -> "Normal"
            bmi < 30.0 -> "Overweight"
            else -> "Obese"
        }
    }

    /**
     * Get health advice based on BMI category
     */
    fun getHealthAdvice(category: String): String {
        return when (category) {
            "Underweight" -> "Increase calorie intake with nutrient-dense foods like nuts, avocados, and lean proteins."
            "Normal" -> "Maintain your balanced diet and regular activity. Great job!"
            "Overweight" -> "Consider more physical activity and portion control. Focus on whole foods."
            "Obese" -> "Adopt a healthier diet and exercise plan. Consult a healthcare provider."
            else -> "Maintain a balanced lifestyle with proper nutrition and exercise."
        }
    }
    enum class ActivityLevel(val DisplayName: String,val description: String, val multiplier: Double) {
        SEDENTARY("Sedentary","Little to no exercise", 1.2),
        LIGHT("Light","Light exercise 1-3 days/week", 1.375),
        MODERATE("Moderate","Moderate exercise 3-5 days/week", 1.55),
        ACTIVE("Active","Hard exercise 6-7 days/week", 1.725),
        VERY_ACTIVE("Very Active","Very hard exercise & physical job", 1.9);

        companion object {
            fun fromString(name: String): ActivityLevel {
                return entries.firstOrNull { it.name == name } ?: SEDENTARY    //SEDENTARY DEFAULT IF
                                                                                // SOMETHINGS WRONG
            }
        }

    }
    enum class WeightGoal {
        LOSE, MAINTAIN, GAIN;

        companion object {
            fun fromString(name: String): WeightGoal {
                return WeightGoal.entries.firstOrNull { it.name == name } ?: LOSE   //LOSE DEFAULT IF
                                                                                        // SOMETHINGS WRONG
            }
        }
    }

}