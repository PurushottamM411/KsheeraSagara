package com.example.ksheerasagara.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ksheerasagara.data.model.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date LIKE :month || '%'")
    fun getExpensesByMonth(month: String): Flow<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses WHERE date LIKE :month || '%'")
    suspend fun getTotalExpenseForMonth(month: String): Double?

    @Query("SELECT SUM(amount) FROM expenses WHERE category = :category AND date LIKE :month || '%'")
    suspend fun getExpenseByCategory(category: String, month: String): Double?

    @Query("SELECT SUM(amount) FROM expenses WHERE cowId = :cowId AND date LIKE :month || '%'")
    suspend fun getTotalExpenseByCowAndMonth(cowId: Int, month: String): Double?
}