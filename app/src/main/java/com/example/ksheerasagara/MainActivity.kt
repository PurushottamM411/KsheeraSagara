package com.example.ksheerasagara

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ksheerasagara.ui.SplashScreen
import com.example.ksheerasagara.ui.cow.CowScreen
import com.example.ksheerasagara.ui.dashboard.DashboardScreen
import com.example.ksheerasagara.ui.expense.ExpenseScreen
import com.example.ksheerasagara.ui.income.IncomeScreen
import com.example.ksheerasagara.ui.theme.KsheeraSagaraTheme
import com.example.ksheerasagara.viewmodel.MainViewModel

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Home)
    object Income : Screen("income", "Income", Icons.Default.Add)
    object Expense : Screen("expense", "Expense", Icons.Default.List)
    object Cows : Screen("cows", "My Cows", Icons.Default.Star)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KsheeraSagaraTheme {
                var showSplash by remember { mutableStateOf(true) }
                if (showSplash) {
                    SplashScreen(onFinished = { showSplash = false })
                } else {
                    KsheeraSagaraApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KsheeraSagaraApp() {
    val viewModel: MainViewModel = viewModel()
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }
    val screens = listOf(Screen.Dashboard, Screen.Income, Screen.Expense, Screen.Cows)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (currentScreen) {
                            Screen.Dashboard -> "Profit Dashboard"
                            Screen.Income -> "Income Log"
                            Screen.Expense -> "Expense Log"
                            Screen.Cows -> "My Cows"
                            else -> "Ksheera-Sagara"
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFF1B5E20),
                    titleContentColor = androidx.compose.ui.graphics.Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar {
                screens.forEach { screen ->
                    NavigationBarItem(
                        selected = currentScreen == screen,
                        onClick = { currentScreen = screen },
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentScreen,
            modifier = Modifier.padding(innerPadding),
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
            },
            label = "screen_transition"
        ) { screen ->
            when (screen) {
                Screen.Dashboard -> DashboardScreen(viewModel = viewModel)
                Screen.Income -> IncomeScreen(viewModel = viewModel)
                Screen.Expense -> ExpenseScreen(viewModel = viewModel)
                Screen.Cows -> CowScreen(viewModel = viewModel)
                else -> DashboardScreen(viewModel = viewModel)
            }
        }
    }
}