package com.example.ksheerasagara.ui.cow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ksheerasagara.data.model.Cow
import com.example.ksheerasagara.viewmodel.CowAnalysis
import com.example.ksheerasagara.viewmodel.MainViewModel

@Composable
fun CowScreen(viewModel: MainViewModel) {
    val cows by viewModel.allCows.collectAsStateWithLifecycle(initialValue = emptyList())
    val cowAnalysisList by viewModel.cowAnalysisList.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var showAddForm by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadCowAnalysis()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Cows",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(onClick = { showAddForm = !showAddForm }) {
                    Text(if (showAddForm) "Cancel" else "+ Add Cow")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Add cow form
        if (showAddForm) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Add New Cow", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Cow Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = breed,
                            onValueChange = { breed = it },
                            label = { Text("Breed (HF, Gir, Jersey)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                if (name.isNotEmpty()) {
                                    viewModel.addCow(
                                        Cow(
                                            name = name,
                                            breed = breed,
                                            purchaseDate = java.time.LocalDate.now().toString()
                                        )
                                    )
                                    name = ""
                                    breed = ""
                                    showSuccess = true
                                    showAddForm = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Cow")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (showSuccess) {
            item {
                Text(
                    "Cow added successfully!",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LaunchedEffect(showSuccess) {
                    kotlinx.coroutines.delay(2000)
                    showSuccess = false
                }
            }
        }

        // Analysis cards
        if (cowAnalysisList.isEmpty() && cows.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No cows added yet.\nTap '+ Add Cow' to get started!",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
        } else if (cowAnalysisList.isEmpty()) {
            item {
                Text(
                    "Your Cows",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(cows) { cow ->
                SimpleCowCard(cow)
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            item {
                Text(
                    "Cow Performance Analysis",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Ranked by profitability this month",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(cowAnalysisList.withIndex().toList()) { (index, analysis) ->
                CowAnalysisCard(analysis = analysis, rank = index + 1)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun SimpleCowCard(cow: Cow) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("${cow.name}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Breed: ${cow.breed}", fontSize = 13.sp, color = Color.Gray)
            }
            Text(
                if (cow.isActive) "Active" else "Inactive",
                color = if (cow.isActive) Color(0xFF2E7D32) else Color.Gray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun CowAnalysisCard(analysis: CowAnalysis, rank: Int) {
    val bgColor = when (analysis.recommendationColor) {
        "green" -> Color(0xFFE8F5E9)
        "yellow" -> Color(0xFFFFFDE7)
        "red" -> Color(0xFFFFEBEE)
        else -> Color(0xFFF5F5F5)
    }
    val accentColor = when (analysis.recommendationColor) {
        "green" -> Color(0xFF2E7D32)
        "yellow" -> Color(0xFFF57F17)
        "red" -> Color(0xFFC62828)
        else -> Color.Gray
    }
    val rankEmoji = when (rank) {
        1 -> "🥇"
        2 -> "🥈"
        3 -> "🥉"
        else -> "#$rank"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgColor)
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(rankEmoji, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    analysis.cow.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Text(
                                    analysis.cow.breed,
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "Rs ${"%.0f".format(analysis.netProfit)}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = accentColor
                            )
                            Text("Net Profit", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }

            // Stats
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MiniStat("Income", "Rs ${"%.0f".format(analysis.totalIncome)}", Color(0xFF1565C0))
                    MiniStat("Expense", "Rs ${"%.0f".format(analysis.totalExpense)}", Color(0xFFB71C1C))
                    MiniStat("Liters", "${"%.1f".format(analysis.totalLiters)} L", Color(0xFF00695C))
                    MiniStat("Per Liter", "Rs ${"%.1f".format(analysis.profitPerLiter)}", Color(0xFF4527A0))
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(10.dp))

                // Recommendation
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(accentColor, RoundedCornerShape(50))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        analysis.recommendation,
                        fontSize = 14.sp,
                        color = accentColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun MiniStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = color)
        Text(label, fontSize = 11.sp, color = Color.Gray)
    }
}