package com.example.ksheerasagara.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ksheerasagara.data.model.MilkEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MilkEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilkEntry(entry: MilkEntry)

    @Delete
    suspend fun deleteMilkEntry(entry: MilkEntry)

    @Query("SELECT * FROM milk_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<MilkEntry>>

    @Query("SELECT * FROM milk_entries WHERE date LIKE :month || '%'")
    fun getEntriesByMonth(month: String): Flow<List<MilkEntry>>

    @Query("SELECT * FROM milk_entries WHERE cowId = :cowId")
    fun getEntriesByCow(cowId: Int): Flow<List<MilkEntry>>

    @Query("SELECT SUM(incomeAmount) FROM milk_entries WHERE date LIKE :month || '%'")
    suspend fun getTotalIncomeForMonth(month: String): Double?

    @Query("SELECT SUM(liters) FROM milk_entries WHERE date LIKE :month || '%'")
    suspend fun getTotalLitersForMonth(month: String): Double?

    @Query("SELECT SUM(incomeAmount) FROM milk_entries WHERE cowId = :cowId AND date LIKE :month || '%'")
    suspend fun getTotalIncomeByCowAndMonth(cowId: Int, month: String): Double?

    @Query("SELECT SUM(liters) FROM milk_entries WHERE cowId = :cowId AND date LIKE :month || '%'")
    suspend fun getTotalLitersByCowAndMonth(cowId: Int, month: String): Double?
}