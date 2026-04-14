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
    version = 4,           // bump version
    exportSchema = false
)
abstract class FoodDatabase : RoomDatabase() {

    abstract val dao: FoodDao
    abstract val dailyLogDao: DailyLogDao  // add this
    abstract val dietPlanDao: DietPlanDao

    companion object {
        @Volatile
        private var INSTANCE: FoodDatabase? = null

        // migration 2 → 3 (DailyLog and FoodEntry tables)
        val MIGRATION_2_3 = object : androidx.room.migration.Migration(2, 3) {
            override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                database.execSQL("""
                CREATE TABLE IF NOT EXISTS `daily_log` (
                    `logId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `userId` INTEGER NOT NULL,
                    `date` TEXT NOT NULL
                )
            """)
                database.execSQL("""
                CREATE TABLE IF NOT EXISTS `food_entry` (
                    `entryId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `logOwnerId` INTEGER NOT NULL,
                    `foodId` INTEGER NOT NULL,
                    `mealType` TEXT NOT NULL,
                    `servingSize` REAL NOT NULL,
                    FOREIGN KEY (`logOwnerId`) REFERENCES `daily_log`(`logId`) ON DELETE CASCADE
                )
            """)
            }
        }

        // migration 3 → 4 (DietPlan table)
        val MIGRATION_3_4 = object : androidx.room.migration.Migration(3, 4) {
            override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
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
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)  // ✅ both migrations
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}