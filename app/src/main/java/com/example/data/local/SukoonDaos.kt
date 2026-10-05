package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AuditLogEntry
import com.example.data.model.ChatMessage
import com.example.data.model.JournalEntry
import com.example.data.model.MoodEntry
import com.example.data.model.ScheduledCheckIn
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearAllMessages()

    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteMessage(id: Long)
}

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<JournalEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: JournalEntry): Long

    @Delete
    suspend fun deleteEntry(entry: JournalEntry)
}

@Dao
interface MoodDao {
    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC")
    fun getAllMoodEntries(): Flow<List<MoodEntry>>

    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC LIMIT 7")
    fun getRecentMoodEntries(): Flow<List<MoodEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMood(entry: MoodEntry): Long
}

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM scheduled_check_ins ORDER BY hour ASC, minute ASC")
    fun getAllSchedules(): Flow<List<ScheduledCheckIn>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: ScheduledCheckIn): Long

    @Update
    suspend fun updateSchedule(schedule: ScheduledCheckIn)

    @Delete
    suspend fun deleteSchedule(schedule: ScheduledCheckIn)
}

@Dao
interface AuditDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 50")
    fun getAllLogs(): Flow<List<AuditLogEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogEntry): Long
}
