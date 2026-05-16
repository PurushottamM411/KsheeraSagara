package com.example.ksheerasagara.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ksheerasagara.utils.PdfExporter
import com.example.ksheerasagara.viewmodel.MainViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val context = LocalContext.current

    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")

    LaunchedEffect(currentYearMonth) {
        viewModel.loadMonthlyStats(currentYearMonth.toString())
    }

    val totalIncome by viewModel.totalIncome.collectAsStateWithLifecycle()
    val totalExpense by viewModel.totalExpense.collectAsStateWithLifecycle()
    val netProfitLoss by viewModel.netProfitLoss.collectAsStateWithLifecycle()
    val totalLiters by viewModel.totalLiters.collectAsStateWithLifecycle()
    val profitPerLiter by viewModel.profitPerLiter.collectAsStateWithLifecycle()
    val fodder by viewModel.fodderExpense.collectAsStateWithLifecycle()
    val medical by viewModel.medicalExpense.collectAsStateWithLifecycle()
    val labor by viewModel.laborExpense.collectAsStateWithLifecycle()

    val isProfit = netProfitLoss >= 0
    val healthColor = if (isProfit) Color(0xFF2E7D32) else Color(0xFFC62828)
    val healthBg = if (isProfit) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val healthLabel = if (isProfit) "PROFIT" else "LOSS"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Month selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2E7D32)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    currentYearMonth = currentYearMonth.minusMonths(1)
                }) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Previous Month",
                        tint = Color.White
                    )
                }
                Text(
                    text = currentYearMonth.format(monthFormatter),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                IconButton(
                    onClick = {
                        if (currentYearMonth < YearMonth.now()) {
                            currentYearMonth = currentYearMonth.plusMonths(1)
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "Next Month",
                        tint = if (currentYearMonth < YearMonth.now())
                            Color.White else Color(0xFF81C784)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Health indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(healthBg, RoundedCornerShape(12.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = healthLabel,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = healthColor
                )
                Text(
                    text = "Rs ${"%.2f".format(netProfitLoss)}",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = healthColor
                )
                Text(
                    "Net Profit / Loss",
                    color = healthColor,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                "Total Income",
                "Rs ${"%.0f".format(totalIncome)}",
                Color(0xFF1565C0),
                Modifier.weight(1f)
            )
            StatCard(
                "Total Expense",
                "Rs ${"%.0f".format(totalExpense)}",
                Color(0xFFB71C1C),
                Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                "Total Liters",
                "${"%.1f".format(totalLiters)} L",
                Color(0xFF00695C),
                Modifier.weight(1f)
            )
            StatCard(
                "Profit / Liter",
                "Rs ${"%.2f".format(profitPerLiter)}",
                Color(0xFF4527A0),
                Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Expense breakdown
        Text(
            "Expense Breakdown",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        ExpenseBar("Fodder", fodder, totalExpense)
        ExpenseBar("Medical", medical, totalExpense)
        ExpenseBar("Labor", labor, totalExpense)

        Spacer(modifier = Modifier.height(24.dp))

        // Export button
        Button(
            onClick = {
                PdfExporter.generateAndShare(
                    context = context,
                    month = currentYearMonth.toString(),
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    netProfitLoss = netProfitLoss,
                    totalLiters = totalLiters,
                    profitPerLiter = profitPerLiter,
                    fodder = fodder,
                    medical = medical,
                    labor = labor
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1B5E20)
            )
        ) {
            Text(
                "Share Monthly Report (PDF / WhatsApp)",
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
            Text(label, fontSize = 12.sp)
        }
    }
}

@Composable
fun ExpenseBar(label: String, amount: Double, total: Double) {
    val percent = if (total > 0) (amount / total).toFloat() else 0f
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 14.sp)
            Text(
                "Rs ${"%.0f".format(amount)} (${"%.0f".format(percent * 100)}%)",
                fontSize = 14.sp
            )
        }
        LinearProgressIndicator(
            progress = { percent },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .padding(top = 2.dp)
        )
    }
}