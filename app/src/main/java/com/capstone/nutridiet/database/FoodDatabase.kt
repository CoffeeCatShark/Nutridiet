package com.capstone.nutridiet.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        FoodData::class,
        DailyLog::class,   // add this
        FoodEntry::class,   // add this
        DietPlan::class
    ],
    version = 3,           // bump version
    exportSchema = false
)
abstract class FoodDatabase : RoomDatabase() {

    abstract val dao: FoodDao
    abstract val dailyLogDao: DailyLogDao  // add this
    abstract val dietPlanDao: DietPlanDao
    companion object {
        @Volatile
        private var INSTANCE: FoodDatabase? = null

        // ✅ add this migration
        val MIGRATION_2_3 = object : androidx.room.migration.Migration(2, 3) {
            override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                // only creates the new diet_plan table
                // leaves FoodData, DailyLog, FoodEntry untouched ✅
                database.execSQL("""
                CREATE TABLE IF NOT EXISTS `diet_plan` (
                    `planId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `bloodPressureReading` INTEGER NOT NULL,
                    `hypertensionLevel` TEXT NOT NULL,
                    `generatedPlan` TEXT NOT NULL,
                    `generatedAt` TEXT NOT NULL
                )
            """)
            }
        }

        fun getDatabase(context: Context): FoodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FoodDatabase::class.java,
                    "nutridietFood_db"
                )
                    .createFromAsset("initialDatabase/FoodData.db")
                    .addMigrations(MIGRATION_2_3)  // ✅
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}