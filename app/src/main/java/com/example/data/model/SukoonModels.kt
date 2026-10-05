package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SenderType {
    USER,
    SUKOON
}

enum class SukoonMode(val displayName: String, val subtitle: String) {
    COMPANION("Offline Companion", "Loving, caring & warm zone for Ashu"),
    STUDY("Online Study & Knowledge", "Smart mentor for Accounts, Business & Economics")
}

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sender: String, // "USER" or "SUKOON"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val mode: String = "COMPANION",
    val isProactive: Boolean = false,
    val audioSnippet: String? = null
)

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val mood: String = "Peaceful",
    val isEncrypted: Boolean = true
)

@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val moodRating: Int, // 1 to 5
    val moodName: String, // e.g. "Loved", "Serene", "Stressed", "Exhausted"
    val stressScore: Int, // 0 to 100
    val note: String
)

@Entity(tableName = "scheduled_check_ins")
data class ScheduledCheckIn(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val promptMessage: String,
    val hour: Int,
    val minute: Int,
    val isEnabled: Boolean = true,
    val intervalMinutes: Int = 120, // automatic intervals
    val triggerType: String = "TIME_INTERVAL" // TIME_INTERVAL, STRESS_TRIGGER, MORNING_WAKEUP, NIGHT_CHECKIN
)

@Entity(tableName = "audit_logs")
data class AuditLogEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val action: String,
    val actor: String = "Ashu (Beby)",
    val detail: String
)

data class BiometricReading(
    val heartRateBpm: Int = 74,
    val hrvMs: Int = 62,
    val stressScore: Int = 38, // 0 - 100
    val sleepQualityHours: Float = 7.5f,
    val isWearableConnected: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val isStressElevated: Boolean
        get() = stressScore >= 70
}
