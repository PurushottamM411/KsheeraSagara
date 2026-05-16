package com.example.ksheerasagara.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cowId: Int,
    val date: String,
    val amount: Double,
    val category: String,
    val note: String = ""
)