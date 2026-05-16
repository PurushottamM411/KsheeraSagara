package com.example.ksheerasagara.ui.expense

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ksheerasagara.data.model.Expense
import com.example.ksheerasagara.viewmodel.MainViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(viewModel: MainViewModel) {
    val expenses by viewModel.allExpenses.collectAsStateWithLifecycle(initialValue = emptyList())
    val categories = listOf("Fodder", "Medical", "Labor", "Other")

    var selectedCategory by remember { mutableStateOf("Fodder") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "💰 Expense Log",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat) },
                        onClick = { selectedCategory = cat; expanded = false }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount (₹)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Note (optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val a = amount.toDoubleOrNull() ?: 0.0
                if (a > 0) {
                    viewModel.addExpense(
                        Expense(
                            cowId = 0,
                            date = LocalDate.now().toString(),
                            amount = a,
                            category = selectedCategory,
                            note = note
                        )
                    )
                    amount = ""; note = ""
                    showSuccess = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Expense ✅")
        }

        if (showSuccess) {
            Text(
                "Expense saved!",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
            LaunchedEffect(showSuccess) {
                kotlinx.coroutines.delay(2000)
                showSuccess = false
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        Text("Recent Expenses", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(expenses.take(10)) { expense ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(expense.category, fontWeight = FontWeight.Medium)
                            Text(expense.date, fontSize = 13.sp)
                            if (expense.note.isNotEmpty())
                                Text(expense.note, fontSize = 12.sp)
                        }
                        Text(
                            "₹${expense.amount}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}