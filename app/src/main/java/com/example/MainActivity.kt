package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.SukoonViewModel
import com.example.ui.screens.AnalyticsAuditScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.JournalScreen
import com.example.ui.screens.ScheduleScreen
import com.example.ui.screens.StudyMentorScreen
import com.example.ui.screens.WellnessScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SukoonNight
import com.example.ui.theme.SukoonRoseLight
import com.example.ui.theme.SukoonRosePrimary
import com.example.ui.theme.SukoonSurface

enum class AppScreen(val label: String, val icon: ImageVector) {
    CHAT("Sukoon", Icons.Default.ChatBubble),
    STUDY("Study", Icons.Default.School),
    WELLNESS("Wellness", Icons.Default.SelfImprovement),
    JOURNAL("Journal", Icons.Default.Lock),
    SCHEDULE("Auto-Care", Icons.Default.Alarm),
    ANALYTICS("Security", Icons.Default.Assessment)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SukoonApp()
            }
        }
    }
}

@Composable
fun SukoonApp(viewModel: SukoonViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf(AppScreen.CHAT) }

    // Android back handler to return to home chat screen
    if (currentScreen != AppScreen.CHAT) {
        BackHandler {
            currentScreen = AppScreen.CHAT
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(SukoonNight)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        val isWideScreen = maxWidth >= 600.dp

        if (isWideScreen) {
            // Adaptive Tablet Layout with NavigationRail
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    containerColor = SukoonSurface,
                    contentColor = Color.White
                ) {
                    AppScreen.entries.forEach { screen ->
                        NavigationRailItem(
                            selected = currentScreen == screen,
                            onClick = { currentScreen = screen },
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.label
                                )
                            },
                            label = { Text(screen.label, fontSize = 11.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = SukoonRoseLight,
                                indicatorColor = SukoonRosePrimary,
                                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                                unselectedTextColor = Color.White.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier.testTag("nav_rail_${screen.name.lowercase()}")
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    ScreenContent(
                        screen = currentScreen,
                        viewModel = viewModel,
                        onNavigateToChatWithPrompt = { prompt ->
                            currentScreen = AppScreen.CHAT
                            viewModel.sendMessage(prompt)
                        },
                        onNavigateToChat = {
                            currentScreen = AppScreen.CHAT
                        }
                    )
                }
            }
        } else {
            // Mobile Compact Layout with bottom NavigationBar
            Scaffold(
                containerColor = SukoonNight,
                contentWindowInsets = WindowInsets(0.dp),
                bottomBar = {
                    NavigationBar(
                        containerColor = SukoonSurface,
                        contentColor = Color.White,
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .testTag("bottom_navigation_bar")
                    ) {
                        AppScreen.entries.forEach { screen ->
                            NavigationBarItem(
                                selected = currentScreen == screen,
                                onClick = { currentScreen = screen },
                                icon = {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = screen.label
                                    )
                                },
                                label = { Text(screen.label, fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = SukoonRoseLight,
                                    indicatorColor = SukoonRosePrimary,
                                    unselectedIconColor = Color.White.copy(alpha = 0.6f),
                                    unselectedTextColor = Color.White.copy(alpha = 0.6f)
                                ),
                                modifier = Modifier.testTag("nav_${screen.name.lowercase()}")
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    ScreenContent(
                        screen = currentScreen,
                        viewModel = viewModel,
                        onNavigateToChatWithPrompt = { prompt ->
                            currentScreen = AppScreen.CHAT
                            viewModel.sendMessage(prompt)
                        },
                        onNavigateToChat = {
                            currentScreen = AppScreen.CHAT
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenContent(
    screen: AppScreen,
    viewModel: SukoonViewModel,
    onNavigateToChatWithPrompt: (String) -> Unit,
    onNavigateToChat: () -> Unit
) {
    when (screen) {
        AppScreen.CHAT -> ChatScreen(viewModel = viewModel)
        AppScreen.STUDY -> StudyMentorScreen(
            viewModel = viewModel,
            onNavigateToChatWithPrompt = onNavigateToChatWithPrompt
        )
        AppScreen.WELLNESS -> WellnessScreen(viewModel = viewModel)
        AppScreen.JOURNAL -> JournalScreen(viewModel = viewModel)
        AppScreen.SCHEDULE -> ScheduleScreen(
            viewModel = viewModel,
            onNavigateToChat = onNavigateToChat
        )
        AppScreen.ANALYTICS -> AnalyticsAuditScreen(viewModel = viewModel)
    }
}
