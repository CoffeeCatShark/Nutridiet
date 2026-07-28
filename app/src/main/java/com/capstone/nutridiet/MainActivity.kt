package com.capstone.nutridiet

import com.capstone.nutridiet.viewmodels.InsightViewModelFactory
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capstone.nutridiet.database.BMIDatabase
import com.capstone.nutridiet.database.FoodDatabase
import com.capstone.nutridiet.notification.BPNotificationScheduler
import com.capstone.nutridiet.ui.theme.NutridietTheme
import com.capstone.nutridiet.utils.PreferencesManager
import com.capstone.nutridiet.viewmodels.BMIViewModel
import com.capstone.nutridiet.viewmodels.BMIViewModelFactory
import com.capstone.nutridiet.viewmodels.CalorieViewModel
import com.capstone.nutridiet.viewmodels.CalorieViewModelFactory
import com.capstone.nutridiet.viewmodels.DailyLogViewModel
import com.capstone.nutridiet.viewmodels.DailyLogViewModelFactory
import com.capstone.nutridiet.viewmodels.FoodViewModel
import com.capstone.nutridiet.viewmodels.FoodViewModelFactory
import com.capstone.nutridiet.viewmodels.InsightViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // permission granted — schedule notification
            BPNotificationScheduler.scheduleNextNotification(this)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // ✅ request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // already granted — schedule
                    BPNotificationScheduler.scheduleNextNotification(this)
                }
                else -> {
                    // request permission
                    requestPermissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                }
            }
        } else {
            // Android 12 and below — no permission needed
            BPNotificationScheduler.scheduleNextNotification(this)
        }
        //val chatViewModel = ViewModelProvider(this)[InsightViewModel::class.java]


        setContent {
            NutridietTheme {
                val context = LocalContext.current

                val prefsManager = remember { PreferencesManager(context) }
                val isFirstLaunch by prefsManager.isFirstLaunch.collectAsState(initial = null)

                val bmiDB = BMIDatabase.getDatabase(context) // get singleton Room database
                val bmiDAO = bmiDB.dao


                val foodDB = FoodDatabase.getDatabase(context)
                val foodDao = foodDB.dao
                val dailyLogDao = foodDB.dailyLogDao
                val bpDao = foodDB.bpLogDao
                val chatViewModel: InsightViewModel = viewModel(
                    factory = InsightViewModelFactory(bmiDAO, foodDao, bpDao, context)
                )
            ////////////////////////////////////////////////////////////////////////////

                val bmiVM: BMIViewModel = viewModel(factory = BMIViewModelFactory(bmiDAO))
                val calorieVM: CalorieViewModel = viewModel(factory = CalorieViewModelFactory(bmiDAO))
                val foodVM: FoodViewModel = viewModel(factory = FoodViewModelFactory(foodDao))
                val dailyLogVM: DailyLogViewModel = viewModel(factory = DailyLogViewModelFactory(
                    dailyLogDao,
                    foodDao
                )
                )

                // Initialize BMI row once when app starts
                LaunchedEffect(Unit) {
                    bmiVM.initialize()       // inserts default row if missing
                    calorieVM.initialize()   // loads activity level or other defaults
                    chatViewModel.initialize()
                }

                if (isFirstLaunch != null) {
                    MainScreen(
                        chatViewModel = chatViewModel,
                        isFirstLaunch = isFirstLaunch!!,
                        onFirstLaunchComplete = {
                            MainScope().launch {
                                prefsManager.setFirstLaunchComplete()
                            }
                        }
                    )
                }

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
    4. CHATBOT(PROBABLY NEEDS DATABASE) - DONE MAYBE [ADD MAYBE THE DATA OF FOOD]
    5. CONSUMPTION THING TAB(NEEDS DATABASE) - -DONE
    6. DASHBOARD COMPLETION - DONE
    FINAL STUFFS : USE CASES, CLEAN THE CODE FROM TESTINGS
    7. ADD SODIUM AND SAT FAT COUNTER/LIMITS
    8. CONNECT AI INSIGHTS TO CURRENT USER DATA(SODIUM COUNTERS)


    UPDATED///////////////////////////
    >>>>>>>>>>>>>>>>>>>>>>>>>>>[IMPORTANT] Add RAG Pipeline<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    9. ADD BMI SCREEN AND CALORIE SCREEN TO DASHBOARD
    10. Add sodium levels
    11. Add BMI Inch Height Converters
    12. change to non american length standards (lbs to kg, g, etc.)
    13. MAKE TEXT LARGER

////
Fetch the whole FoodData
Read fields from it
val food = dao.GetFoodById(id)
val calories = food?.foodCalories
val protein = food?.foodProtein
///

 */