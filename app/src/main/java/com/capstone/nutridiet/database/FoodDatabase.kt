package com.capstone.nutridiet.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        FoodData::class,
        DailyLog::class,
        FoodEntry::class,
        DietPlan::class,
        BloodPressureLog::class
    ],
    version = 6,
    exportSchema = false
)
abstract class FoodDatabase : RoomDatabase() {

    abstract val dao: FoodDao
    abstract val dailyLogDao: DailyLogDao
    abstract val dietPlanDao: DietPlanDao
    abstract val bpLogDao: BloodPressureDao

    companion object {
        @Volatile
        private var INSTANCE: FoodDatabase? = null

        // ✅ migration 2 → 3
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
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

        // ✅ migration 3 → 4
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
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

        // ✅ migration 4 → 5 — now correctly outside MIGRATION_3_4
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `blood_pressure_log` (
                        `logId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `userId` INTEGER NOT NULL,
                        `systolic` INTEGER NOT NULL,
                        `diastolic` INTEGER NOT NULL,
                        `hypertensionLevel` TEXT NOT NULL,
                        `date` TEXT NOT NULL
                    )
                """)
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
            ALTER TABLE diet_plan ADD COLUMN planDays INTEGER NOT NULL DEFAULT 5
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
                    .addMigrations(
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_6// ✅ added here
                    )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}