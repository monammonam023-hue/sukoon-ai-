package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ChatMessage
import com.example.data.model.SukoonMode
import com.example.ui.SukoonViewModel
import com.example.ui.components.ProactiveBanner
import com.example.ui.components.SukoonAvatarBadge
import com.example.ui.theme.SukoonCardBorder
import com.example.ui.theme.SukoonLavender
import com.example.ui.theme.SukoonNight
import com.example.ui.theme.SukoonRoseLight
import com.example.ui.theme.SukoonRosePrimary
import com.example.ui.theme.SukoonSurface
import com.example.ui.theme.SukoonSurfaceVariant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    viewModel: SukoonViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val currentMode by viewModel.currentMode.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val proactiveAlert by viewModel.proactiveAlert.collectAsStateWithLifecycle()

    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SukoonNight)
            .imePadding()
    ) {
        // Top Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SukoonSurface,
            shadowElevation = 3.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SukoonAvatarBadge(size = 42)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Sukoon AI",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4ADE80))
                                )
                            }
                            Text(
                                text = "Always here for Ashu (Beby) ❤️",
                                color = SukoonRoseLight,
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.clearChatHistory() },
                        modifier = Modifier.testTag("clear_chat_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear Chat",
                            tint = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Dual Mode Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SukoonSurfaceVariant)
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ModeButton(
                        title = "Companion Mode",
                        icon = Icons.Default.Favorite,
                        isSelected = currentMode == SukoonMode.COMPANION,
                        accentColor = SukoonRosePrimary,
                        modifier = Modifier.weight(1f),
                        testTag = "companion_mode_button",
                        onClick = { viewModel.setMode(SukoonMode.COMPANION) }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    ModeButton(
                        title = "Study & Knowledge",
                        icon = Icons.Default.School,
                        isSelected = currentMode == SukoonMode.STUDY,
                        accentColor = Color(0xFFA78BFA),
                        modifier = Modifier.weight(1f),
                        testTag = "study_mode_button",
                        onClick = { viewModel.setMode(SukoonMode.STUDY) }
                    )
                }
            }
        }

        // Proactive Alert Banner (if any)
        AnimatedVisibility(visible = proactiveAlert != null) {
            proactiveAlert?.let { alertText ->
                ProactiveBanner(
                    message = alertText,
                    onDismiss = { viewModel.dismissProactiveAlert() }
                )
            }
        }

        // Quick Suggestion Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val suggestions = if (currentMode == SukoonMode.STUDY) {
                listOf(
                    "📌 Golden Rules of Accounting",
                    "📊 Accounting Equation (Assets = L + C)",
                    "📋 Fayol's 14 Principles summary",
                    "📈 Law of Demand & Elasticity",
                    "💡 Explain Inflation & GDP",
                    "📝 Bank Reconciliation (BRS) tips"
                )
            } else {
                listOf(
                    "Beby, I had a stressful day today...",
                    "Sukoon, listen to my day meri jaan ❤️",
                    "I am feeling tired and anxious",
                    "Can we do a relaxing breathing exercise?",
                    "Tell me something loving, Beby",
                    "How to stay peaceful during exams?"
                )
            }

            items(suggestions) { prompt ->
                SuggestionChip(
                    text = prompt,
                    onClick = {
                        val cleanPrompt = prompt.replace("📌 ", "").replace("📊 ", "").replace("📋 ", "").replace("📈 ", "").replace("💡 ", "").replace("📝 ", "")
                        viewModel.sendMessage(cleanPrompt)
                    }
                )
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                MessageBubble(
                    message = msg,
                    onCopy = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Sukoon AI", msg.text))
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    onSpeak = {
                        Toast.makeText(context, "Playing soothing voice...", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            if (isGenerating) {
                item {
                    ThinkingIndicator()
                }
            }
        }

        // Bottom Input Field
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SukoonSurface,
            tonalElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = {
                        Text(
                            text = if (currentMode == SukoonMode.STUDY)
                                "Ask study concept (Accounts, BSt, Eco)..."
                            else
                                "Talk to Sukoon, Beby... ❤️",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = SukoonSurfaceVariant,
                        unfocusedContainerColor = SukoonSurfaceVariant,
                        focusedBorderColor = SukoonRosePrimary,
                        unfocusedBorderColor = SukoonCardBorder
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(SukoonRosePrimary, Color(0xFFC026D3))
                            )
                        )
                        .clickable {
                            if (textInput.isNotBlank()) {
                                viewModel.sendMessage(textInput)
                                textInput = ""
                            }
                        }
                        .testTag("send_message_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send Message",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ModeButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) accentColor.copy(alpha = 0.25f) else Color.Transparent)
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) accentColor else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 10.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) accentColor else Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun SuggestionChip(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("suggestion_chip_$text"),
        color = SukoonSurfaceVariant,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SukoonCardBorder)
    ) {
        Text(
            text = text,
            color = SukoonLavender,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    onCopy: () -> Unit,
    onSpeak: () -> Unit
) {
    val isUser = message.sender == "USER"
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            if (!isUser) {
                SukoonAvatarBadge(size = 32)
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(
                modifier = Modifier.widthIn(max = 310.dp),
                horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
            ) {
                // Header tags for Sukoon message
                if (!isUser) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 3.dp)
                    ) {
                        Text(
                            text = "Sukoon",
                            color = SukoonRoseLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (message.isProactive) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = Color(0xFF6B21A8),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Auto Check-in",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }

                // Bubble Body
                Surface(
                    color = if (isUser) Color(0xFF831843) else SukoonSurfaceVariant,
                    shape = RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isUser) 18.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 18.dp
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isUser) Color(0xFFBE185D) else SukoonCardBorder
                    ),
                    modifier = Modifier.testTag(if (isUser) "user_message_bubble" else "sukoon_message_bubble")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = message.text,
                            color = Color.White,
                            fontSize = 14.sp,
                            lineHeight = 21.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formattedTime,
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 10.sp
                            )

                            if (!isUser) {
                                Row {
                                    IconButton(
                                        onClick = onSpeak,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = "Speak voice",
                                            tint = Color.White.copy(alpha = 0.6f),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = onCopy,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy text",
                                            tint = Color.White.copy(alpha = 0.6f),
                                            modifier = Modifier.size(14.dp)
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

@Composable
fun ThinkingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SukoonAvatarBadge(size = 32)
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            color = SukoonSurfaceVariant,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SukoonCardBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = SukoonRosePrimary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Sukoon is thinking for Beby...",
                    color = SukoonLavender,
                    fontSize = 12.sp
                )
            }
        }
    }
}
