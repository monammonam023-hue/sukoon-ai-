package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ScheduledCheckIn
import com.example.ui.SukoonViewModel
import com.example.ui.theme.SukoonCardBorder
import com.example.ui.theme.SukoonLavender
import com.example.ui.theme.SukoonNight
import com.example.ui.theme.SukoonRoseLight
import com.example.ui.theme.SukoonRosePrimary
import com.example.ui.theme.SukoonSurface
import com.example.ui.theme.SukoonSurfaceVariant

@Composable
fun ScheduleScreen(
    viewModel: SukoonViewModel,
    onNavigateToChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val schedules by viewModel.scheduledCheckIns.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }

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
                            .background(Color(0xFF3B82F6).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = "Proactive Reminders",
                            tint = Color(0xFF60A5FA),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Proactive Check-In Engine",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Sukoon initiates loving messages automatically for Beby",
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
            // Explanation Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SukoonSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SukoonCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = SukoonRosePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Sukoon does not wait for you to reach out. She periodically sends affectionate check-ins, study hydration alerts, and stress interventions!",
                            color = Color.White,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Add custom check-in button
            item {
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SukoonRosePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_schedule_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Schedule")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Custom Proactive Interval / Check-in")
                }
            }

            // List of schedules
            items(schedules, key = { it.id }) { schedule ->
                ScheduleItemCard(
                    schedule = schedule,
                    onToggle = { viewModel.toggleSchedule(schedule) },
                    onDelete = { viewModel.deleteSchedule(schedule) },
                    onTestTrigger = {
                        viewModel.sendProactiveMessageNow(schedule)
                        Toast.makeText(context, "Proactive message sent to chat!", Toast.LENGTH_SHORT).show()
                        onNavigateToChat()
                    }
                )
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var message by remember { mutableStateOf("") }
        var hour by remember { mutableIntStateOf(16) }
        var minute by remember { mutableIntStateOf(0) }
        var intervalMinutes by remember { mutableIntStateOf(120) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = SukoonSurface,
            title = {
                Text(
                    text = "New Proactive Companion Check-in",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title (e.g. Afternoon Sweet Hug)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("schedule_title_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = SukoonRosePrimary
                        )
                    )

                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Sukoon's Loving Message for Beby") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("schedule_message_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = SukoonRosePrimary
                        )
                    )

                    Text(
                        text = "Scheduled Time: ${String.format("%02d:%02d", hour, minute)} (Interval: every ${intervalMinutes}m)",
                        color = SukoonLavender,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && message.isNotBlank()) {
                            viewModel.addCustomSchedule(
                                title = title,
                                promptMessage = message,
                                hour = hour,
                                minute = minute,
                                intervalMinutes = intervalMinutes,
                                triggerType = "TIME_INTERVAL"
                            )
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SukoonRosePrimary),
                    modifier = Modifier.testTag("save_schedule_button")
                ) {
                    Text("Save Check-in")
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
fun ScheduleItemCard(
    schedule: ScheduledCheckIn,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onTestTrigger: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("schedule_item_${schedule.id}"),
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = schedule.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Time: ${String.format("%02d:%02d", schedule.hour, schedule.minute)} • Trigger: ${schedule.triggerType}",
                        color = SukoonLavender,
                        fontSize = 12.sp
                    )
                }

                Switch(
                    checked = schedule.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = SukoonRosePrimary,
                        checkedTrackColor = SukoonRosePrimary.copy(alpha = 0.4f),
                        uncheckedThumbColor = Color.White.copy(alpha = 0.6f),
                        uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("schedule_toggle_${schedule.id}")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = SukoonSurface,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "\"${schedule.promptMessage}\"",
                    color = SukoonRoseLight,
                    fontSize = 13.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onTestTrigger,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("test_trigger_${schedule.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Test Trigger",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Trigger Now (Test)", fontSize = 11.sp)
                }

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
    }
}
