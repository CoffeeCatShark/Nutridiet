package com.capstone.nutridiet.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserBMIData::class], version = 4, exportSchema = false)
abstract class BMIDatabase : RoomDatabase() {

    abstract val dao: UserBMIDAO

    companion object {
        @Volatile
        private var INSTANCE: BMIDatabase? = null

        fun getDatabase(context: Context): BMIDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BMIDatabase::class.java,
                    "nutridiet_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}