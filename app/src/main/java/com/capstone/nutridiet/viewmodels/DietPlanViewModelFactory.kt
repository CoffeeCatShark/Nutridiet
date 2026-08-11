import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.nutridiet.database.BloodPressureDao
import com.capstone.nutridiet.database.DietPlanDao
import com.capstone.nutridiet.database.FoodDao
import com.capstone.nutridiet.database.UserBMIDAO
import com.capstone.nutridiet.network.FirestoreRepository
import com.capstone.nutridiet.viewmodels.DietPlanViewModel

class DietPlanViewModelFactory(
    private val dietPlanDao: DietPlanDao,
    private val foodDao: FoodDao,
    private val userDAO: UserBMIDAO,
    private val bpLogDao: BloodPressureDao,
    private val context: Context,  // ✅ add this
    private val firestoreRepo: FirestoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DietPlanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DietPlanViewModel(
                dietPlanDao, foodDao, userDAO, bpLogDao, context, firestoreRepo
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}