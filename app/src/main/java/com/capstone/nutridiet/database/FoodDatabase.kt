package com.capstone.nutridiet.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        FoodData::class,
        DailyLog::class,   // add this
        FoodEntry::class   // add this
    ],
    version = 2,           // bump version
    exportSchema = false
)
abstract class FoodDatabase : RoomDatabase() {

    abstract val dao: FoodDao
    abstract val dailyLogDao: DailyLogDao  // add this

    companion object {
        @Volatile
        private var INSTANCE: FoodDatabase? = null

        fun getDatabase(context: Context): FoodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FoodDatabase::class.java,
                    "nutridietFood_db"
                )
                    .createFromAsset("initialDatabase/FoodData.db")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}