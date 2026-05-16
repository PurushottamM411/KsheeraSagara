package com.example.ksheerasagara.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ksheerasagara.data.model.*

@Database(
    entities = [Cow::class, MilkEntry::class, Expense::class, MonthlySummary::class],
    version = 1,
    exportSchema = false
)
abstract class KsheeraSagaraDatabase : RoomDatabase() {

    abstract fun cowDao(): CowDao
    abstract fun milkEntryDao(): MilkEntryDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: KsheeraSagaraDatabase? = null

        fun getDatabase(context: Context): KsheeraSagaraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KsheeraSagaraDatabase::class.java,
                    "ksheera_sagara_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}