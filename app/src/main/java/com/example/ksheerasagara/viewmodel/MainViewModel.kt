package com.example.ksheerasagara.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ksheerasagara.data.db.KsheeraSagaraDatabase
import com.example.ksheerasagara.data.model.Cow
import com.example.ksheerasagara.data.model.Expense
import com.example.ksheerasagara.data.model.MilkEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class CowAnalysis(
    val cow: Cow,
    val totalIncome: Double,
    val totalLiters: Double,
    val totalExpense: Double,
    val netProfit: Double,
    val profitPerLiter: Double,
    val recommendation: String,
    val recommendationColor: String // "green", "yellow", "red"
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = KsheeraSagaraDatabase.getDatabase(application)
    private val milkDao = db.milkEntryDao()
    private val expenseDao = db.expenseDao()
    private val cowDao = db.cowDao()

    private val currentMonth = LocalDate.now().toString().substring(0, 7)

    val allCows = cowDao.getAllActiveCows()
    val allMilkEntries = milkDao.getAllEntries()
    val allExpenses = expenseDao.getAllExpenses()

    private val _totalIncome = MutableStateFlow(0.0)
    val totalIncome: StateFlow<Double> = _totalIncome

    private val _totalExpense = MutableStateFlow(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense

    private val _netProfitLoss = MutableStateFlow(0.0)
    val netProfitLoss: StateFlow<Double> = _netProfitLoss

    private val _totalLiters = MutableStateFlow(0.0)
    val totalLiters: StateFlow<Double> = _totalLiters

    private val _profitPerLiter = MutableStateFlow(0.0)
    val profitPerLiter: StateFlow<Double> = _profitPerLiter

    private val _fodderExpense = MutableStateFlow(0.0)
    val fodderExpense: StateFlow<Double> = _fodderExpense

    private val _medicalExpense = MutableStateFlow(0.0)
    val medicalExpense: StateFlow<Double> = _medicalExpense

    private val _laborExpense = MutableStateFlow(0.0)
    val laborExpense: StateFlow<Double> = _laborExpense

    private val _cowAnalysisList = MutableStateFlow<List<CowAnalysis>>(emptyList())
    val cowAnalysisList: StateFlow<List<CowAnalysis>> = _cowAnalysisList

    init {
        loadMonthlyStats(currentMonth)
    }

    fun loadMonthlyStats(month: String) {
        viewModelScope.launch {
            val income = milkDao.getTotalIncomeForMonth(month) ?: 0.0
            val expense = expenseDao.getTotalExpenseForMonth(month) ?: 0.0
            val liters = milkDao.getTotalLitersForMonth(month) ?: 0.0
            val fodder = expenseDao.getExpenseByCategory("Fodder", month) ?: 0.0
            val medical = expenseDao.getExpenseByCategory("Medical", month) ?: 0.0
            val labor = expenseDao.getExpenseByCategory("Labor", month) ?: 0.0

            _totalIncome.value = income
            _totalExpense.value = expense
            _netProfitLoss.value = income - expense
            _totalLiters.value = liters
            _profitPerLiter.value = if (liters > 0) (income - expense) / liters else 0.0
            _fodderExpense.value = fodder
            _medicalExpense.value = medical
            _laborExpense.value = labor
        }
    }

    fun loadCowAnalysis(month: String = currentMonth) {
        viewModelScope.launch {
            val cows = cowDao.getAllActiveCows()
            cows.collect { cowList ->
                val analysisList = cowList.map { cow ->
                    val income = milkDao.getTotalIncomeByCowAndMonth(cow.id, month) ?: 0.0
                    val liters = milkDao.getTotalLitersByCowAndMonth(cow.id, month) ?: 0.0
                    val expense = expenseDao.getTotalExpenseByCowAndMonth(cow.id, month) ?: 0.0
                    val netProfit = income - expense
                    val profitPerLiter = if (liters > 0) netProfit / liters else 0.0

                    val recommendation = when {
                        liters == 0.0 -> "No milk recorded this month"
                        profitPerLiter >= 15 -> "Excellent performer! Keep this cow."
                        profitPerLiter >= 8 -> "Good performer. Monitor feed costs."
                        profitPerLiter >= 0 -> "Low profit. Reduce fodder cost."
                        else -> "Making a loss! Consider selling this cow."
                    }

                    val recommendationColor = when {
                        liters == 0.0 -> "yellow"
                        profitPerLiter >= 15 -> "green"
                        profitPerLiter >= 8 -> "green"
                        profitPerLiter >= 0 -> "yellow"
                        else -> "red"
                    }

                    CowAnalysis(
                        cow = cow,
                        totalIncome = income,
                        totalLiters = liters,
                        totalExpense = expense,
                        netProfit = netProfit,
                        profitPerLiter = profitPerLiter,
                        recommendation = recommendation,
                        recommendationColor = recommendationColor
                    )
                }.sortedByDescending { it.netProfit }

                _cowAnalysisList.value = analysisList
            }
        }
    }

    fun addMilkEntry(entry: MilkEntry) = viewModelScope.launch {
        milkDao.insertMilkEntry(entry)
        loadMonthlyStats(currentMonth)
        loadCowAnalysis(currentMonth)
    }

    fun addExpense(expense: Expense) = viewModelScope.launch {
        expenseDao.insertExpense(expense)
        loadMonthlyStats(currentMonth)
        loadCowAnalysis(currentMonth)
    }

    fun addCow(cow: Cow) = viewModelScope.launch {
        cowDao.insertCow(cow)
        loadCowAnalysis(currentMonth)
    }

    fun deleteMilkEntry(entry: MilkEntry) = viewModelScope.launch {
        milkDao.deleteMilkEntry(entry)
        loadMonthlyStats(currentMonth)
    }

    fun deleteExpense(expense: Expense) = viewModelScope.launch {
        expenseDao.deleteExpense(expense)
        loadMonthlyStats(currentMonth)
    }
}