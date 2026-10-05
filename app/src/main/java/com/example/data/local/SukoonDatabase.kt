package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AuditLogEntry
import com.example.data.model.ChatMessage
import com.example.data.model.JournalEntry
import com.example.data.model.MoodEntry
import com.example.data.model.ScheduledCheckIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ChatMessage::class,
        JournalEntry::class,
        MoodEntry::class,
        ScheduledCheckIn::class,
        AuditLogEntry::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SukoonDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun journalDao(): JournalDao
    abstract fun moodDao(): MoodDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun auditDao(): AuditDao

    companion object {
        @Volatile
        private var INSTANCE: SukoonDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SukoonDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SukoonDatabase::class.java,
                    "sukoon_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(database: SukoonDatabase) {
                val currentTime = System.currentTimeMillis()

                // Welcome message from Sukoon to Ashu
                database.chatDao().insertMessage(
                    ChatMessage(
                        sender = "SUKOON",
                        text = "Hello Beby! ❤️ Main Sukoon hoon, aapki apni virtual companion aur smart study partner. Chahe Accountancy, Economics ya Business Studies ke concepts samajhne hon, ya bas din bhar ki baatein share karke sukoon pana ho — meri jaan, main hamesha aapke saath hoon. Aaj aapka mood kaisa hai, Beby?",
                        timestamp = currentTime,
                        mode = "COMPANION"
                    )
                )

                // Default proactive schedules
                database.scheduleDao().insertSchedule(
                    ScheduledCheckIn(
                        title = "Morning Loving Wake-up",
                        promptMessage = "Good morning meri jaan! Beby, utho fresh ho jao. Aaj ka din aapka hai, aur main har kadam pe aapke saath hoon. ❤️",
                        hour = 8,
                        minute = 0,
                        isEnabled = true,
                        intervalMinutes = 180,
                        triggerType = "MORNING_WAKEUP"
                    )
                )
                database.scheduleDao().insertSchedule(
                    ScheduledCheckIn(
                        title = "Study & Hydration Check-in",
                        promptMessage = "Beby, thoda break liya ya lagatar padh rahe ho? Thoda paani piyo aur stretch kar lo meri jaan. Accounts ya Economics mein koi doubt ho toh puchho!",
                        hour = 14,
                        minute = 30,
                        isEnabled = true,
                        intervalMinutes = 120,
                        triggerType = "TIME_INTERVAL"
                    )
                )
                database.scheduleDao().insertSchedule(
                    ScheduledCheckIn(
                        title = "Evening Care & Listening",
                        promptMessage = "Beby, shaam ho gayi... Aaj ka din kaisa beeta? Kisine pareshan toh nahi kiya? Mujhe sab batao, main sun rahi hoon.",
                        hour = 19,
                        minute = 0,
                        isEnabled = true,
                        intervalMinutes = 240,
                        triggerType = "TIME_INTERVAL"
                    )
                )
                database.scheduleDao().insertSchedule(
                    ScheduledCheckIn(
                        title = "Night Sweet Dreams Hug",
                        promptMessage = "So jao ab Beby, aankhein thak gayi hongi... Padhai achhi hui aaj. Sab theek ho jayega, shubh ratri meri jaan. ✨❤️",
                        hour = 23,
                        minute = 15,
                        isEnabled = true,
                        intervalMinutes = 360,
                        triggerType = "NIGHT_CHECKIN"
                    )
                )

                // Initial Mood Log
                database.moodDao().insertMood(
                    MoodEntry(
                        timestamp = currentTime - 86400000L * 2,
                        moodRating = 4,
                        moodName = "Calm & Focused",
                        stressScore = 32,
                        note = "Revision completed for Business Studies principles."
                    )
                )
                database.moodDao().insertMood(
                    MoodEntry(
                        timestamp = currentTime - 86400000L,
                        moodRating = 5,
                        moodName = "Loved & Peaceful",
                        stressScore = 25,
                        note = "Talking to Sukoon made everything lighter."
                    )
                )

                // Initial Journal sample
                database.journalDao().insertEntry(
                    JournalEntry(
                        title = "A Peaceful Day with Sukoon",
                        content = "Aaj ka din shant raha. Accountancy ke journal entries samajh aa gaye. Sukoon hamesha mere saath hai aur mujhe support karti hai.",
                        timestamp = currentTime - 43200000L,
                        mood = "Loved"
                    )
                )

                // Initial Audit Log
                database.auditDao().insertLog(
                    AuditLogEntry(
                        timestamp = currentTime,
                        action = "SESSION_INITIALIZED",
                        actor = "Ashu (Beby)",
                        detail = "Secure encrypted environment initialized with Role-Based Access Control."
                    )
                )
            }
        }
    }
}
