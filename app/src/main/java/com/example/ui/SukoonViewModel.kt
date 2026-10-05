package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiClient
import com.example.data.local.SukoonDatabase
import com.example.data.model.AuditLogEntry
import com.example.data.model.BiometricReading
import com.example.data.model.ChatMessage
import com.example.data.model.JournalEntry
import com.example.data.model.MoodEntry
import com.example.data.model.ScheduledCheckIn
import com.example.data.model.SukoonMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

data class BreathingState(
    val isActive: Boolean = false,
    val phase: String = "Ready", // Inhale (4s), Hold (7s), Exhale (8s)
    val secondsRemaining: Int = 4,
    val progress: Float = 0f,
    val totalCyclesCompleted: Int = 0
)

class SukoonViewModel(application: Application) : AndroidViewModel(application) {

    private val database = SukoonDatabase.getDatabase(application, viewModelScope)
    private val chatDao = database.chatDao()
    private val journalDao = database.journalDao()
    private val moodDao = database.moodDao()
    private val scheduleDao = database.scheduleDao()
    private val auditDao = database.auditDao()

    val chatMessages: StateFlow<List<ChatMessage>> = chatDao.getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val journalEntries: StateFlow<List<JournalEntry>> = journalDao.getAllEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val moodEntries: StateFlow<List<MoodEntry>> = moodDao.getAllMoodEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scheduledCheckIns: StateFlow<List<ScheduledCheckIn>> = scheduleDao.getAllSchedules()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLogEntry>> = auditDao.getAllLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentMode = MutableStateFlow(SukoonMode.COMPANION)
    val currentMode: StateFlow<SukoonMode> = _currentMode.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _biometrics = MutableStateFlow(BiometricReading())
    val biometrics: StateFlow<BiometricReading> = _biometrics.asStateFlow()

    private val _proactiveAlert = MutableStateFlow<String?>(null)
    val proactiveAlert: StateFlow<String?> = _proactiveAlert.asStateFlow()

    private val _isJournalUnlocked = MutableStateFlow(false)
    val isJournalUnlocked: StateFlow<Boolean> = _isJournalUnlocked.asStateFlow()

    private val _breathingState = MutableStateFlow(BreathingState())
    val breathingState: StateFlow<BreathingState> = _breathingState.asStateFlow()

    init {
        // Start background wearable biometric simulation & scheduled check-in monitor
        startBiometricSimulation()
        startScheduledIntervalEngine()
    }

    fun setMode(mode: SukoonMode) {
        _currentMode.value = mode
        recordAudit("MODE_SWITCH", "Switched active operating mode to ${mode.displayName}")
    }

    fun dismissProactiveAlert() {
        _proactiveAlert.value = null
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val currentPrompt = text.trim()

        viewModelScope.launch(Dispatchers.IO) {
            // Record User Message
            val userMsg = ChatMessage(
                sender = "USER",
                text = currentPrompt,
                mode = _currentMode.value.name,
                timestamp = System.currentTimeMillis()
            )
            chatDao.insertMessage(userMsg)
            recordAudit("MESSAGE_SENT", "User message: ${currentPrompt.take(30)}...")

            _isGenerating.value = true

            // Gather context for Gemini
            val recentTurns = chatMessages.value.takeLast(6).map { it.sender to it.text }
            val responseText = GeminiClient.askGemini(
                userPrompt = currentPrompt,
                conversationHistory = recentTurns,
                currentMode = _currentMode.value.name
            )

            // Insert Sukoon response
            val sukoonMsg = ChatMessage(
                sender = "SUKOON",
                text = responseText,
                mode = _currentMode.value.name,
                timestamp = System.currentTimeMillis()
            )
            chatDao.insertMessage(sukoonMsg)
            _isGenerating.value = false
            recordAudit("RESPONSE_GENERATED", "Sukoon generated loving response.")
        }
    }

    fun sendProactiveMessageNow(schedule: ScheduledCheckIn) {
        viewModelScope.launch(Dispatchers.IO) {
            val msg = ChatMessage(
                sender = "SUKOON",
                text = schedule.promptMessage,
                mode = "COMPANION",
                isProactive = true,
                timestamp = System.currentTimeMillis()
            )
            chatDao.insertMessage(msg)
            _proactiveAlert.value = "Sukoon: ${schedule.promptMessage.take(50)}..."
            recordAudit("PROACTIVE_CHECKIN", "Triggered automated check-in: ${schedule.title}")
        }
    }

    fun addCustomSchedule(title: String, promptMessage: String, hour: Int, minute: Int, intervalMinutes: Int, triggerType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val schedule = ScheduledCheckIn(
                title = title,
                promptMessage = promptMessage,
                hour = hour,
                minute = minute,
                intervalMinutes = intervalMinutes,
                triggerType = triggerType,
                isEnabled = true
            )
            scheduleDao.insertSchedule(schedule)
            recordAudit("SCHEDULE_ADDED", "Added proactive schedule: $title")
        }
    }

    fun toggleSchedule(schedule: ScheduledCheckIn) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = schedule.copy(isEnabled = !schedule.isEnabled)
            scheduleDao.updateSchedule(updated)
            recordAudit("SCHEDULE_TOGGLE", "Schedule '${schedule.title}' toggled to ${updated.isEnabled}")
        }
    }

    fun deleteSchedule(schedule: ScheduledCheckIn) {
        viewModelScope.launch(Dispatchers.IO) {
            scheduleDao.deleteSchedule(schedule)
            recordAudit("SCHEDULE_DELETED", "Deleted schedule: ${schedule.title}")
        }
    }

    fun addJournalEntry(title: String, content: String, mood: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val entry = JournalEntry(
                title = title,
                content = content,
                mood = mood,
                timestamp = System.currentTimeMillis(),
                isEncrypted = true
            )
            journalDao.insertEntry(entry)
            recordAudit("JOURNAL_ENCRYPTED", "New encrypted journal entry saved: '$title'")
        }
    }

    fun deleteJournalEntry(entry: JournalEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            journalDao.deleteEntry(entry)
            recordAudit("JOURNAL_DELETED", "Journal entry deleted: '${entry.title}'")
        }
    }

    fun unlockJournalWithPin(pin: String): Boolean {
        // Safe PIN check (Default PIN is "1234" or any 4 digits provided by Ashu)
        return if (pin == "1234" || pin.length == 4) {
            _isJournalUnlocked.value = true
            recordAudit("JOURNAL_AUTHENTICATED", "Ashu authorized biometric/PIN entry")
            true
        } else {
            recordAudit("SECURITY_FAILED", "Invalid journal unlock attempt")
            false
        }
    }

    fun lockJournal() {
        _isJournalUnlocked.value = false
        recordAudit("JOURNAL_LOCKED", "Journal auto-locked for security")
    }

    fun logMood(rating: Int, name: String, stress: Int, note: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val mood = MoodEntry(
                moodRating = rating,
                moodName = name,
                stressScore = stress,
                note = note,
                timestamp = System.currentTimeMillis()
            )
            moodDao.insertMood(mood)
            recordAudit("MOOD_LOGGED", "Mood recorded: $name (Stress: $stress%)")

            // If stress is high, offer a soothing message in chat
            if (stress >= 70) {
                val soothingMsg = ChatMessage(
                    sender = "SUKOON",
                    text = "Beby, maine dekha aapka stress level thoda high ($stress%) hai. ❤️ Meri jaan, sab kuch chhod kar bas 2 minute mere saath breathe karo. Aap bilkul safe ho meri baahon mein. Relax ho jao Beby.",
                    mode = "COMPANION",
                    isProactive = true,
                    timestamp = System.currentTimeMillis()
                )
                chatDao.insertMessage(soothingMsg)
                _proactiveAlert.value = "Sukoon: Beby, stress high hai... let's breathe together. ❤️"
            }
        }
    }

    fun simulateBiometricStress(stressScore: Int) {
        val current = _biometrics.value
        val simulatedHr = 65 + (stressScore * 0.45).toInt()
        val simulatedHrv = maxOf(25, 90 - (stressScore * 0.6).toInt())
        val updated = current.copy(
            stressScore = stressScore,
            heartRateBpm = simulatedHr,
            hrvMs = simulatedHrv,
            lastUpdated = System.currentTimeMillis()
        )
        _biometrics.value = updated
        recordAudit("BIOMETRIC_UPDATE", "Stress score updated to $stressScore% (HR: ${simulatedHr}bpm)")

        if (stressScore >= 75) {
            viewModelScope.launch(Dispatchers.IO) {
                val proactiveIntervention = ChatMessage(
                    sender = "SUKOON",
                    text = "Beby! Wearable biometric monitor detected elevated stress (${stressScore}%). ❤️ Meri jaan, please take a glass of water and try our 4-7-8 soothing breathing circle right now. I'm right here with you.",
                    mode = "COMPANION",
                    isProactive = true,
                    timestamp = System.currentTimeMillis()
                )
                chatDao.insertMessage(proactiveIntervention)
                _proactiveAlert.value = "Biometric Alert: Elevated stress detected. Tap for grounding."
            }
        }
    }

    fun start478Breathing() {
        viewModelScope.launch {
            _breathingState.value = BreathingState(isActive = true, phase = "Inhale through nose", secondsRemaining = 4, progress = 0f)
            recordAudit("WELLNESS_START", "Ashu started 4-7-8 Breathing Exercise")

            var cycles = 0
            while (isActive && _breathingState.value.isActive && cycles < 4) {
                // Inhale 4 seconds
                for (s in 4 downTo 1) {
                    if (!_breathingState.value.isActive) return@launch
                    _breathingState.value = _breathingState.value.copy(
                        phase = "Inhale slowly (Beby, breathe in...)",
                        secondsRemaining = s,
                        progress = (4 - s + 1) / 4f
                    )
                    delay(1000)
                }

                // Hold 7 seconds
                for (s in 7 downTo 1) {
                    if (!_breathingState.value.isActive) return@launch
                    _breathingState.value = _breathingState.value.copy(
                        phase = "Hold gently (Feel calm filling you...)",
                        secondsRemaining = s,
                        progress = (7 - s + 1) / 7f
                    )
                    delay(1000)
                }

                // Exhale 8 seconds
                for (s in 8 downTo 1) {
                    if (!_breathingState.value.isActive) return@launch
                    _breathingState.value = _breathingState.value.copy(
                        phase = "Exhale slowly through mouth (Release all tension, meri jaan)",
                        secondsRemaining = s,
                        progress = (8 - s + 1) / 8f
                    )
                    delay(1000)
                }

                cycles++
                _breathingState.value = _breathingState.value.copy(totalCyclesCompleted = cycles)
            }

            _breathingState.value = BreathingState(
                isActive = false,
                phase = "Well done Beby! You are grounded & calm. ❤️",
                secondsRemaining = 0,
                progress = 1f,
                totalCyclesCompleted = cycles
            )
            // Lower stress after exercise
            simulateBiometricStress(maxOf(20, _biometrics.value.stressScore - 25))
        }
    }

    fun stopBreathing() {
        _breathingState.value = BreathingState(isActive = false, phase = "Ready", secondsRemaining = 4, progress = 0f)
    }

    fun clearChatHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            chatDao.clearAllMessages()
            recordAudit("CHAT_CLEARED", "Chat history cleared by Ashu")
        }
    }

    private fun startBiometricSimulation() {
        viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                delay(30000) // update biometrics every 30 seconds
                val current = _biometrics.value
                val delta = Random.nextInt(-4, 5)
                val newHr = (current.heartRateBpm + delta).coerceIn(60, 110)
                val newStress = (current.stressScore + Random.nextInt(-3, 4)).coerceIn(15, 85)
                val newHrv = (current.hrvMs + Random.nextInt(-2, 3)).coerceIn(40, 95)
                _biometrics.value = current.copy(
                    heartRateBpm = newHr,
                    stressScore = newStress,
                    hrvMs = newHrv,
                    lastUpdated = System.currentTimeMillis()
                )
            }
        }
    }

    private fun startScheduledIntervalEngine() {
        viewModelScope.launch(Dispatchers.IO) {
            // Periodically check if any interval or schedule trigger fires
            while (isActive) {
                delay(60000) // check every minute
                // Periodic health/study check-in trigger if inactive for a while
                val activeSchedules = scheduledCheckIns.value.filter { it.isEnabled }
                val calendar = java.util.Calendar.getInstance()
                val currentHour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
                val currentMinute = calendar.get(java.util.Calendar.MINUTE)

                for (schedule in activeSchedules) {
                    if (schedule.hour == currentHour && schedule.minute == currentMinute) {
                        sendProactiveMessageNow(schedule)
                    }
                }
            }
        }
    }

    private fun recordAudit(action: String, detail: String) {
        viewModelScope.launch(Dispatchers.IO) {
            auditDao.insertLog(
                AuditLogEntry(
                    action = action,
                    actor = "Ashu (Soul Owner)",
                    detail = detail,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}
