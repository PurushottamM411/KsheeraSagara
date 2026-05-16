package com.example.ksheerasagara.ui.income

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ksheerasagara.data.model.MilkEntry
import com.example.ksheerasagara.viewmodel.MainViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeScreen(viewModel: MainViewModel) {
    val cows by viewModel.allCows.collectAsStateWithLifecycle(initialValue = emptyList())
    val entries by viewModel.allMilkEntries.collectAsStateWithLifecycle(initialValue = emptyList())

    var selectedCowId by remember { mutableStateOf(0) }
    var liters by remember { mutableStateOf("") }
    var fatPercent by remember { mutableStateOf("") }
    var ratePerLiter by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Milk Income Log",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (cows.isNotEmpty()) {
            var expanded by remember { mutableStateOf(false) }
            val selectedCow = cows.find { it.id == selectedCowId }?.name ?: "Select Cow"
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCow,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Cow") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    cows.forEach { cow ->
                        DropdownMenuItem(
                            text = { Text(cow.name) },
                            onClick = {
                                selectedCowId = cow.id
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = liters,
            onValueChange = { liters = it },
            label = { Text("Liters Produced") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = fatPercent,
            onValueChange = { fatPercent = it },
            label = { Text("Fat Percent (from slip)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = ratePerLiter,
            onValueChange = { ratePerLiter = it },
            label = { Text("Rate per Liter (Rs)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val l = liters.toDoubleOrNull() ?: 0.0
                val f = fatPercent.toDoubleOrNull() ?: 0.0
                val r = ratePerLiter.toDoubleOrNull() ?: 0.0
                if (l > 0 && r > 0) {
                    viewModel.addMilkEntry(
                        MilkEntry(
                            cowId = selectedCowId,
                            date = LocalDate.now().toString(),
                            liters = l,
                            fatPercent = f,
                            ratePerLiter = r,
                            incomeAmount = l * r
                        )
                    )
                    liters = ""
                    fatPercent = ""
                    ratePerLiter = ""
                    showSuccess = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Milk Entry")
        }

        if (showSuccess) {
            Text(
                "Entry saved successfully!",
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

        Text("Recent Entries", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(entries.take(10)) { entry ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(entry.date, fontWeight = FontWeight.Medium)
                            Text(
                                "${entry.liters}L | Fat: ${entry.fatPercent}%",
                                fontSize = 13.sp
                            )
                        }
                        Text(
                            "Rs ${entry.incomeAmount}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}