package com.example.ksheerasagara.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cows")
data class Cow(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val breed: String,
    val purchaseDate: String,
    val isActive: Boolean = true
)