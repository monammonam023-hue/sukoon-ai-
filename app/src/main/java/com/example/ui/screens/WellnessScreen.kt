package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.SukoonViewModel
import com.example.ui.theme.SukoonCardBorder
import com.example.ui.theme.SukoonLavender
import com.example.ui.theme.SukoonNight
import com.example.ui.theme.SukoonRoseLight
import com.example.ui.theme.SukoonRosePrimary
import com.example.ui.theme.SukoonStressHigh
import com.example.ui.theme.SukoonStressLow
import com.example.ui.theme.SukoonStressMedium
import com.example.ui.theme.SukoonSurface
import com.example.ui.theme.SukoonSurfaceVariant

@Composable
fun WellnessScreen(
    viewModel: SukoonViewModel,
    modifier: Modifier = Modifier
) {
    val biometrics by viewModel.biometrics.collectAsStateWithLifecycle()
    val breathingState by viewModel.breathingState.collectAsStateWithLifecycle()

    var activeSound by remember { mutableStateOf<String?>(null) }
    var groundingStep by remember { mutableIntStateOf(5) }

    // Breathing orb scaling animation
    val orbScale by animateFloatAsState(
        targetValue = when {
            !breathingState.isActive -> 1.0f
            breathingState.phase.contains("Inhale") -> 1.55f
            breathingState.phase.contains("Hold") -> 1.55f
            else -> 0.95f
        },
        animationSpec = tween(
            durationMillis = if (breathingState.phase.contains("Inhale")) 4000 else if (breathingState.phase.contains("Exhale")) 8000 else 7000,
            easing = FastOutSlowInEasing
        ),
        label = "BreathingOrbScale"
    )

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
                            .background(SukoonRosePrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = "Wellness",
                            tint = SukoonRosePrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Wearable Health & Serenity",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Real-time stress tracking & voice-guided grounding for Ashu",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Biometrics Dashboard Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("biometrics_card"),
                    shape = RoundedCornerShape(18.dp),
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
                                    imageVector = Icons.Default.Watch,
                                    contentDescription = "Wearable",
                                    tint = SukoonRoseLight,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Wearable Biometrics Live",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (biometrics.isStressElevated) SukoonStressHigh.copy(alpha = 0.2f) else SukoonStressLow.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (biometrics.isStressElevated) "Elevated Stress" else "State: Calm",
                                    color = if (biometrics.isStressElevated) SukoonStressHigh else SukoonStressLow,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            BiometricTile(
                                label = "Heart Rate",
                                value = "${biometrics.heartRateBpm}",
                                unit = "BPM",
                                icon = Icons.Default.Favorite,
                                color = Color(0xFFFB7185)
                            )
                            BiometricTile(
                                label = "HRV (Variability)",
                                value = "${biometrics.hrvMs}",
                                unit = "ms",
                                icon = Icons.Default.GraphicEq,
                                color = Color(0xFF38BDF8)
                            )
                            BiometricTile(
                                label = "Stress Index",
                                value = "${biometrics.stressScore}",
                                unit = "/100",
                                icon = Icons.Default.SelfImprovement,
                                color = if (biometrics.stressScore > 70) SukoonStressHigh else if (biometrics.stressScore > 40) SukoonStressMedium else SukoonStressLow
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stress Indicator bar
                        Text(
                            text = "Physiological Stress Level (${biometrics.stressScore}%)",
                            color = SukoonLavender,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { biometrics.stressScore / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (biometrics.stressScore > 70) SukoonStressHigh else if (biometrics.stressScore > 40) SukoonStressMedium else SukoonStressLow,
                            trackColor = Color.White.copy(alpha = 0.1f),
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stress Simulation / Calibration for Testing Proactive Trigger
                        Text(
                            text = "Calibrate / Simulate Stress Level for Testing Proactive Interventions:",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                        Slider(
                            value = biometrics.stressScore.toFloat(),
                            onValueChange = { viewModel.simulateBiometricStress(it.toInt()) },
                            valueRange = 10f..100f,
                            colors = SliderDefaults.colors(
                                thumbColor = SukoonRosePrimary,
                                activeTrackColor = SukoonRosePrimary,
                                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.testTag("stress_simulator_slider")
                        )
                    }
                }
            }

            // 4-7-8 Breathing Meditation Companion
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("breathing_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SukoonSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SukoonCardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "4-7-8 Grounding & Breathing Orb",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Hands-free soothing audio & rhythmic pacing designed for Ashu",
                            color = SukoonLavender,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Visual Breathing Orb
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .scale(orbScale)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFFF9EAF),
                                            Color(0xFF8B2B50),
                                            Color(0xFF2E132B)
                                        )
                                    )
                                )
                                .border(2.dp, SukoonRoseLight.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (breathingState.isActive) "${breathingState.secondsRemaining}s" else "Breathe",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (breathingState.isActive) {
                                    Text(
                                        text = "Cycle ${breathingState.totalCyclesCompleted + 1}/4",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = breathingState.phase,
                            color = SukoonRoseLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (!breathingState.isActive) {
                                Button(
                                    onClick = { viewModel.start478Breathing() },
                                    colors = ButtonDefaults.buttonColors(containerColor = SukoonRosePrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("start_breathing_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Start",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Start Breathing with Sukoon")
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.stopBreathing() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("stop_breathing_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Stop,
                                        contentDescription = "Stop",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Stop Exercise")
                                }
                            }
                        }
                    }
                }
            }

            // 5-4-3-2-1 Sensory Grounding Tool
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sensory_grounding_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SukoonSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SukoonCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "5-4-3-2-1 Sensory Grounding Technique",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Proven method to halt acute anxiety and panic moments.",
                            color = SukoonLavender,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        val sensorySteps = listOf(
                            5 to "Acknowledge 5 things you can SEE around you right now",
                            4 to "Acknowledge 4 things you can physically TOUCH / FEEL",
                            3 to "Acknowledge 3 distinct sounds you can HEAR",
                            2 to "Acknowledge 2 things you can SMELL around you",
                            1 to "Acknowledge 1 thing you can TASTE or one loving thought"
                        )

                        sensorySteps.forEach { (count, prompt) ->
                            val isDone = groundingStep < count
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { groundingStep = count - 1 }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isDone) Color(0xFF10B981) else SukoonRosePrimary.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDone) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Done",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "$count",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = prompt,
                                    color = if (isDone) Color.White.copy(alpha = 0.5f) else Color.White,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }
            }

            // Calming Ambient Sounds (Simulated hands-free audio companion)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ambient_audio_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SukoonSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SukoonCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Calming Ambient Audio Companion",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Play relaxing natural white-noise and delta waves while studying or relaxing.",
                            color = SukoonLavender,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val sounds = listOf(
                            "Gentle Rain & Chimes" to Icons.Default.WaterDrop,
                            "Deep Night Waves" to Icons.Default.Nightlight,
                            "Theta Relaxation Waves" to Icons.Default.GraphicEq
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            sounds.forEach { (name, icon) ->
                                val isPlaying = activeSound == name
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            activeSound = if (isPlaying) null else name
                                        }
                                        .testTag("ambient_sound_$name"),
                                    color = if (isPlaying) SukoonRosePrimary.copy(alpha = 0.3f) else SukoonSurface,
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isPlaying) SukoonRosePrimary else SukoonCardBorder
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Default.Pause else icon,
                                            contentDescription = name,
                                            tint = if (isPlaying) SukoonRosePrimary else Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = name,
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Normal,
                                            maxLines = 2
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
fun BiometricTile(
    label: String,
    value: String,
    unit: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Surface(
        color = SukoonSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SukoonCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = unit,
                    color = SukoonLavender,
                    fontSize = 10.sp
                )
            }
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 10.sp
            )
        }
    }
}
