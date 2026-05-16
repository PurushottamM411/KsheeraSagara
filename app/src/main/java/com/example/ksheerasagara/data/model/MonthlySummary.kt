package com.example.ksheerasagara.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monthly_summary")
data class MonthlySummary(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val month: String,
    val totalIncome: Double,
    val totalExpense: Double,
    val netProfitLoss: Double,
    val totalLiters: Double,
    val profitPerLiter: Double
)