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
import com.capstone.nutridiet.database.UserBMIDAO
import com.capstone.nutridiet.ui.theme.NutridietTheme
import com.capstone.nutridiet.viewmodels.BMIRepository
import com.capstone.nutridiet.viewmodels.BMIViewModel
import com.capstone.nutridiet.viewmodels.BMIViewModelFactory
import com.capstone.nutridiet.viewmodels.CalorieViewModel
import com.capstone.nutridiet.viewmodels.CalorieViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutridietTheme {
                val context = LocalContext.current
                val bmiDB = BMIDatabase.getDatabase(context) // get singleton Room database
                val bmiDAO = bmiDB.dao

                val bmiVM: BMIViewModel = viewModel(factory = BMIViewModelFactory(bmiDAO))
                val calorieVM: CalorieViewModel = viewModel(factory = CalorieViewModelFactory(bmiDAO))



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
    1. BMI - DONE
    2. CALORIES - DONE
    3. FOOD DATABASE SETUP(NEEDS DATABASE) (FILLING UP TABLE, OFFLOAD TO GROUPMATES??) - NOT YET
    4. CHATBOT(PROBABLY NEEDS DATABASE) - NOT YET
    5. CONSUMPTION THING TAB(NEEDS DATABASE) - NOT YET

    FINAL STUFFS : USE CASES, CLEAN THE CODE FROM TESTINGS

 */