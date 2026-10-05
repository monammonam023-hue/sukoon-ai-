package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SukoonMode
import com.example.ui.SukoonViewModel
import com.example.ui.theme.SukoonCardBorder
import com.example.ui.theme.SukoonLavender
import com.example.ui.theme.SukoonNight
import com.example.ui.theme.SukoonRoseLight
import com.example.ui.theme.SukoonRosePrimary
import com.example.ui.theme.SukoonSurface
import com.example.ui.theme.SukoonSurfaceVariant
import kotlinx.coroutines.delay

data class StudyTopic(
    val id: String,
    val subject: String, // "Accountancy", "Business Studies", "Economics"
    val title: String,
    val subtitle: String,
    val details: String,
    val sampleQuestion: String
)

@Composable
fun StudyMentorScreen(
    viewModel: SukoonViewModel,
    onNavigateToChatWithPrompt: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var expandedTopicId by remember { mutableStateOf<String?>(null) }

    // Pomodoro Timer State
    var isTimerRunning by remember { mutableStateOf(false) }
    var timerSecondsRemaining by remember { mutableIntStateOf(25 * 60) }

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning && timerSecondsRemaining > 0) {
            delay(1000)
            timerSecondsRemaining--
        }
        if (timerSecondsRemaining == 0) {
            isTimerRunning = false
        }
    }

    val topics = remember {
        listOf(
            StudyTopic(
                id = "acc_golden_rules",
                subject = "Accountancy",
                title = "3 Golden Rules of Accounting",
                subtitle = "Personal, Real, Nominal accounts simplified with Hindi & Hinglish logic",
                details = "• Personal: Debit the Receiver, Credit the Giver\n• Real: Debit what comes in, Credit what goes out\n• Nominal: Debit all expenses & losses, Credit all incomes & gains.",
                sampleQuestion = "Explain golden rules of accounting with practical journal entry examples for Beby."
            ),
            StudyTopic(
                id = "acc_equation",
                subject = "Accountancy",
                title = "The Accounting Equation",
                subtitle = "Assets = Liabilities + Capital (The universal balance sheet logic)",
                details = "Every business transaction has dual aspect (debit & credit). If cash is invested, Cash (Asset) increases and Capital increases equally.",
                sampleQuestion = "Explain the fundamental accounting equation Assets = Liabilities + Capital with transactions."
            ),
            StudyTopic(
                id = "bst_fayol",
                subject = "Business Studies",
                title = "Henri Fayol's 14 Principles",
                subtitle = "Management principles: Division of work, Unity of Command, Scalar chain",
                details = "1. Division of Work, 2. Authority & Responsibility, 3. Discipline, 4. Unity of Command, 5. Unity of Direction, 6. Subordination of Individual Interest, 7. Remuneration, 8. Centralization, 9. Scalar Chain, 10. Order, 11. Equity, 12. Stability of Personnel, 13. Initiative, 14. Espirit de Corps.",
                sampleQuestion = "Summarize Henri Fayol's 14 principles of management for exam preparation."
            ),
            StudyTopic(
                id = "bst_functions",
                subject = "Business Studies",
                title = "Functions of Management (POSDCORB)",
                subtitle = "Planning, Organizing, Staffing, Directing, and Controlling",
                details = "• Planning: Deciding in advance what to do and how to do it.\n• Organizing: Grouping activities and allocating resources.\n• Staffing: Right person at the right job.\n• Directing: Motivating, leading, communicating.\n• Controlling: Benchmarking actual results with standards.",
                sampleQuestion = "Explain the 5 core functions of management with real-world examples."
            ),
            StudyTopic(
                id = "eco_demand",
                subject = "Economics",
                title = "Law of Demand & Price Elasticity",
                subtitle = "Inverse relationship between Price and Quantity Demanded (Ed formula)",
                details = "Ed = (% change in Qty Demanded) / (% change in Price).\n• Ed > 1: Elastic (Luxury items)\n• Ed < 1: Inelastic (Essential salt, medicines)\n• Ed = 1: Unitary Elastic.",
                sampleQuestion = "Explain the Law of Demand and Price Elasticity with diagrams for Beby."
            ),
            StudyTopic(
                id = "eco_gdp_inflation",
                subject = "Economics",
                title = "GDP & Inflation Mechanics",
                subtitle = "Gross Domestic Product equation & Central Bank Repo Rate controls",
                details = "GDP = C + I + G + (X - M). Inflation occurs when money loses purchasing power. RBI increases repo rate to make loans costlier, curbing excessive money supply in the market.",
                sampleQuestion = "How does Central Bank control Inflation using Repo Rate and Monetary policy?"
            )
        )
    }

    val filteredTopics = remember(selectedCategory) {
        if (selectedCategory == "All") topics else topics.filter { it.subject == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SukoonNight)
    ) {
        // Screen Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SukoonSurface,
            shadowElevation = 3.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF8B5CF6).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Study Hub",
                            tint = Color(0xFFA78BFA),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Ashu's Study & Knowledge Hub",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Sukoon is your personal high-intelligence mentor",
                            color = SukoonLavender,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Pomodoro Focus Timer Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pomodoro_timer_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SukoonSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SukoonCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = "Focus",
                                    tint = SukoonRosePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Sukoon Focus Timer (Pomodoro)",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                            }

                            val minutes = timerSecondsRemaining / 60
                            val seconds = timerSecondsRemaining % 60
                            Text(
                                text = String.format("%02d:%02d", minutes, seconds),
                                color = SukoonRoseLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isTimerRunning)
                                "Focus session in progress! Beby, you can do this. ❤️"
                            else
                                "25-minute deep focus for Accountancy & Economics revision.",
                            color = SukoonLavender,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    if (isTimerRunning) {
                                        isTimerRunning = false
                                    } else {
                                        if (timerSecondsRemaining == 0) timerSecondsRemaining = 25 * 60
                                        isTimerRunning = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isTimerRunning) Color(0xFFEF4444) else SukoonRosePrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("toggle_study_timer_button")
                            ) {
                                Icon(
                                    imageVector = if (isTimerRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                                    contentDescription = "Start / Pause",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isTimerRunning) "Pause Session" else "Start 25m Focus",
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // Subject Filter Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Accountancy", "Business Studies", "Economics").forEach { category ->
                        val isSelected = selectedCategory == category
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedCategory = category }
                                .testTag("filter_subject_$category"),
                            color = if (isSelected) Color(0xFF8B5CF6) else SukoonSurfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFFA78BFA) else SukoonCardBorder
                            )
                        ) {
                            Text(
                                text = category,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Topics list
            items(filteredTopics, key = { it.id }) { topic ->
                val isExpanded = expandedTopicId == topic.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("study_topic_${topic.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SukoonSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SukoonCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedTopicId = if (isExpanded) null else topic.id
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (topic.subject) {
                                                "Accountancy" -> Color(0xFF10B981).copy(alpha = 0.2f)
                                                "Business Studies" -> Color(0xFF3B82F6).copy(alpha = 0.2f)
                                                else -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (topic.subject) {
                                            "Accountancy" -> Icons.Default.Calculate
                                            "Business Studies" -> Icons.Default.CheckCircle
                                            else -> Icons.Default.TrendingUp
                                        },
                                        contentDescription = topic.subject,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = topic.title,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${topic.subject} • Tap to view summary",
                                        color = SukoonLavender,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            IconButton(onClick = { expandedTopicId = if (isExpanded) null else topic.id }) {
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "Expand",
                                    tint = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column(modifier = Modifier.padding(top = 10.dp)) {
                                Text(
                                    text = topic.subtitle,
                                    color = SukoonRoseLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = topic.details,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        viewModel.setMode(SukoonMode.STUDY)
                                        onNavigateToChatWithPrompt(topic.sampleQuestion)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF7C3AED)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("ask_mentor_${topic.id}")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Ask Sukoon to Explain This to Beby",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.ChevronRight,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
