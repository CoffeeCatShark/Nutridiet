package com.capstone.nutridiet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.capstone.nutridiet.database.BMIDatabase
import com.capstone.nutridiet.database.FoodDatabase
import com.capstone.nutridiet.database.UserBMIDAO
import com.capstone.nutridiet.ui.theme.NutridietTheme
import com.capstone.nutridiet.viewmodels.BMIRepository
import com.capstone.nutridiet.viewmodels.BMIViewModel
import com.capstone.nutridiet.viewmodels.BMIViewModelFactory
import com.capstone.nutridiet.viewmodels.CalorieViewModel
import com.capstone.nutridiet.viewmodels.CalorieViewModelFactory
import com.capstone.nutridiet.viewmodels.FoodViewModel
import com.capstone.nutridiet.viewmodels.FoodViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutridietTheme {
                val context = LocalContext.current
                val bmiDB = BMIDatabase.getDatabase(context) // get singleton Room database
                val bmiDAO = bmiDB.dao

                val foodDB = FoodDatabase.getDatabase(context)
                val foodDao = foodDB.dao

            ////////////////////////////////////////////////////////////////////////////

                val bmiVM: BMIViewModel = viewModel(factory = BMIViewModelFactory(bmiDAO))
                val calorieVM: CalorieViewModel = viewModel(factory = CalorieViewModelFactory(bmiDAO))
                val foodVM: FoodViewModel = viewModel(factory = FoodViewModelFactory(foodDao))


                // Initialize BMI row once when app starts
                LaunchedEffect(Unit) {
                    bmiVM.initialize()       // inserts default row if missing
                    calorieVM.initialize()   // loads activity level or other defaults
                }
                MainScreen()

            }
        }
    }
}
/* TO DO ============ NOTE: THIS IS A SHORT LIST. ACTUAL TASKS ARE VERY VERY VERY LONG.
NOTE: (FILLING UP FOOD DATA TABLE, OFFLOAD TO GROUPMATES, GUIDE THEM)
NOTE: TURN DASHBOARD INTO CHECKING LIST FOR FOOD ITEMS FIRST
    1. BMI - DONE
    2. CALORIES - DONE
    3. FOOD DATABASE SETUP - DONE
    4. CHATBOT(PROBABLY NEEDS DATABASE) - NOT YET
    5. CONSUMPTION THING TAB(NEEDS DATABASE) - NOT YET

    FINAL STUFFS : USE CASES, CLEAN THE CODE FROM TESTINGS

////
Fetch the whole FoodData
Read fields from it
val food = dao.GetFoodById(id)
val calories = food?.foodCalories
val protein = food?.foodProtein
///

 */