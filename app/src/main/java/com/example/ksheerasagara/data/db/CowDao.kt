package com.example.ksheerasagara.data.db

import androidx.room.*
import com.example.ksheerasagara.data.model.Cow
import kotlinx.coroutines.flow.Flow

@Dao
interface CowDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCow(cow: Cow)

    @Update
    suspend fun updateCow(cow: Cow)

    @Delete
    suspend fun deleteCow(cow: Cow)

    @Query("SELECT * FROM cows WHERE isActive = 1")
    fun getAllActiveCows(): Flow<List<Cow>>

    @Query("SELECT * FROM cows")
    fun getAllCows(): Flow<List<Cow>>
}