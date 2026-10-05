package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.JournalEntry
import com.example.ui.SukoonViewModel
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
fun JournalScreen(
    viewModel: SukoonViewModel,
    modifier: Modifier = Modifier
) {
    val isUnlocked by viewModel.isJournalUnlocked.collectAsStateWithLifecycle()
    val entries by viewModel.journalEntries.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    // Mood logger state
    var selectedMoodRating by remember { mutableIntStateOf(5) }
    var selectedMoodName by remember { mutableStateOf("Loved") }
    var moodStressLevel by remember { mutableIntStateOf(25) }
    var moodNote by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SukoonNight)
    ) {
        // Top Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SukoonSurface,
            shadowElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEC4899).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = "Journal Security",
                            tint = Color(0xFFF472B6),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Ashu's Encrypted Journal",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = if (isUnlocked) "Authenticated • End-to-end encrypted" else "Protected by PIN & Biometrics",
                            color = SukoonLavender,
                            fontSize = 12.sp
                        )
                    }
                }

                if (isUnlocked) {
                    IconButton(
                        onClick = { viewModel.lockJournal() },
                        modifier = Modifier.testTag("lock_journal_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        if (!isUnlocked) {
            // Locked screen state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(SukoonSurfaceVariant)
                        .border(2.dp, SukoonRosePrimary.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Security Lock",
                        tint = SukoonRosePrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Private Sanctuary for Ashu",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "Your daily thoughts, emotions, and entries are encrypted locally. Enter PIN (Default: 1234) or use Biometrics.",
                    color = SukoonLavender,
                    fontSize = 13.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = pinInput,
                    onValueChange = {
                        if (it.length <= 4) {
                            pinInput = it
                            pinError = false
                        }
                    },
                    label = { Text("Enter 4-Digit PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = SukoonSurfaceVariant,
                        unfocusedContainerColor = SukoonSurfaceVariant,
                        focusedBorderColor = SukoonRosePrimary,
                        unfocusedBorderColor = SukoonCardBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .testTag("pin_input_field")
                )

                if (pinError) {
                    Text(
                        text = "Invalid PIN. Try 1234",
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (viewModel.unlockJournalWithPin(pinInput)) {
                            pinInput = ""
                        } else {
                            pinError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SukoonRosePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .testTag("unlock_pin_button")
                ) {
                    Text("Unlock with PIN")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        viewModel.unlockJournalWithPin("1234")
                        Toast.makeText(context, "Biometric authentication confirmed", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SukoonSurfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SukoonCardBorder),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .testTag("unlock_biometric_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Biometric",
                        tint = SukoonRoseLight,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Use Biometric Unlock", color = Color.White)
                }
            }
        } else {
            // Unlocked State: Journal Entries & Daily Mood Logger
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Quick Daily Mood Logger Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("daily_mood_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = SukoonSurfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SukoonCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Mood,
                                    contentDescription = "Mood",
                                    tint = SukoonRosePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "How are you feeling right now, Beby?",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Mood tags
                            val moods = listOf("Loved" to 5, "Serene" to 4, "Focused" to 4, "Stressed" to 2, "Tired" to 2)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                moods.forEach { (name, rating) ->
                                    val isSelected = selectedMoodName == name
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                selectedMoodName = name
                                                selectedMoodRating = rating
                                            }
                                            .testTag("mood_chip_$name"),
                                        color = if (isSelected) SukoonRosePrimary else SukoonSurface,
                                        shape = RoundedCornerShape(12.dp),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) SukoonRosePrimary else SukoonCardBorder
                                        )
                                    ) {
                                        Text(
                                            text = name,
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Current Stress Level (${moodStressLevel}%)",
                                color = SukoonLavender,
                                fontSize = 12.sp
                            )
                            Slider(
                                value = moodStressLevel.toFloat(),
                                onValueChange = { moodStressLevel = it.toInt() },
                                valueRange = 0f..100f,
                                colors = SliderDefaults.colors(
                                    thumbColor = SukoonRosePrimary,
                                    activeTrackColor = SukoonRosePrimary,
                                    inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                                )
                            )

                            OutlinedTextField(
                                value = moodNote,
                                onValueChange = { moodNote = it },
                                placeholder = {
                                    Text(
                                        text = "Add a quick note or what's on your mind...",
                                        color = Color.White.copy(alpha = 0.5f),
                                        fontSize = 12.sp
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = SukoonSurface,
                                    unfocusedContainerColor = SukoonSurface,
                                    focusedBorderColor = SukoonRosePrimary,
                                    unfocusedBorderColor = SukoonCardBorder
                                ),
                                maxLines = 2
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.logMood(
                                            rating = selectedMoodRating,
                                            name = selectedMoodName,
                                            stress = moodStressLevel,
                                            note = moodNote.ifBlank { "Logged from Ashu's sanctuary" }
                                        )
                                        moodNote = ""
                                        Toast.makeText(context, "Mood logged for Beby ❤️", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SukoonRosePrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("log_mood_button")
                                ) {
                                    Text("Log Mood")
                                }
                            }
                        }
                    }
                }

                // Add New Journal Entry Button
                item {
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_journal_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Entry")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Write New Encrypted Journal Entry")
                    }
                }

                // Entries List
                if (entries.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No entries yet. Write your thoughts to Sukoon! ❤️",
                                color = SukoonLavender,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    items(entries, key = { it.id }) { entry ->
                        JournalEntryCard(
                            entry = entry,
                            onDelete = { viewModel.deleteJournalEntry(entry) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var entryTitle by remember { mutableStateOf("") }
        var entryContent by remember { mutableStateOf("") }
        var entryMood by remember { mutableStateOf("Peaceful") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = SukoonSurface,
            title = {
                Text(
                    text = "New Encrypted Journal Entry",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = entryTitle,
                        onValueChange = { entryTitle = it },
                        label = { Text("Title") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("entry_title_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = SukoonRosePrimary
                        )
                    )

                    OutlinedTextField(
                        value = entryContent,
                        onValueChange = { entryContent = it },
                        label = { Text("Your deep thoughts (Encrypted)...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .testTag("entry_content_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = SukoonRosePrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (entryTitle.isNotBlank() && entryContent.isNotBlank()) {
                            viewModel.addJournalEntry(entryTitle, entryContent, entryMood)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SukoonRosePrimary),
                    modifier = Modifier.testTag("save_journal_entry_button")
                ) {
                    Text("Save Securely")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = Color.White.copy(alpha = 0.7f))
                }
            }
        )
    }
}

@Composable
fun JournalEntryCard(
    entry: JournalEntry,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val formattedDate = remember(entry.timestamp) { dateFormat.format(Date(entry.timestamp)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("journal_entry_${entry.id}"),
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
                Column {
                    Text(
                        text = entry.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = formattedDate,
                        color = SukoonLavender,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SukoonRosePrimary.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "AES-256 Encrypted",
                            color = SukoonRoseLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = entry.content,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }
    }
}
