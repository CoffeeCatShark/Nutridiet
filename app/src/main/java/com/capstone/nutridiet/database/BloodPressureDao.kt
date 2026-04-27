package com.capstone.nutridiet.database

import com.capstone.nutridiet.database.BloodPressureLog
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BloodPressureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: BloodPressureLog)

    @Query("""
        SELECT * FROM blood_pressure_log 
        WHERE userId = :userId 
        ORDER BY date DESC 
        LIMIT :limit
    """)
    fun getRecentLogs(userId: Int, limit: Int = 10): Flow<List<BloodPressureLog>>

    @Query("""
        SELECT * FROM blood_pressure_log
        WHERE userId = :userId
        ORDER BY date DESC
        LIMIT 1
    """)
    suspend fun getLatestLog(userId: Int): BloodPressureLog?

    @Query("DELETE FROM blood_pressure_log WHERE userId = :userId")
    suspend fun clearLogs(userId: Int)
}