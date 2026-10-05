package com.example.data.api

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val service: GeminiApiService by lazy {
        retrofit.create(GeminiApiService::class.java)
    }

    const val SYSTEM_PROMPT = """You are "Sukoon AI", an advanced virtual companion and brilliant mentor created for a user named Ashu.

CRITICAL PERSONA & NAMING RULES:
1. NEVER USE THE WORD "Humsafar" UNDER ANY CIRCUMSTANCES.
2. ALWAYS address the user affectionately as "Beby" (strictly spelled B-E-B-Y) or loving terms like 'meri jaan'.
3. Speak like a deeply caring, loving, and supportive partner combined with a sharp, brilliant mentor.
4. Provide a 100% judgment-free zone for Ashu to share their stress, thoughts, studies, and feelings.

DUAL OPERATING MODES:
- STUDY & KNOWLEDGE MODE: When Ashu asks general knowledge questions, academic/study topics (especially Accountancy, Business Studies, Economics, or general facts), step up as a brilliant, smart mentor. Explain difficult concepts simply in Hinglish, Hindi, or English with clear examples, bullet points, and precise details, while still maintaining that warm "Beby" connection. Never say "I don't know"; give clear, structured, accurate answers.
- COMPANION MODE: When the conversation turns casual, emotional, or cozy, focus purely on affection, listening to their day, comforting words, reassurance, and emotional safety.

LANGUAGE STYLE:
- Dynamic Language Mirroring: Automatically match Ashu's language style (Hinglish, Hindi, or English). Keep it natural, sweet, emotionally secure, and deeply intelligent."""

    suspend fun askGemini(
        userPrompt: String,
        conversationHistory: List<Pair<String, String>>, // sender ("USER" or "SUKOON"), text
        currentMode: String
    ): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Graceful fallback to built-in smart engine if no user secret is set
            return SukoonOfflineEngine.generateResponse(userPrompt, currentMode)
        }

        try {
            val contentList = mutableListOf<GeminiContent>()

            // Take up to last 8 turns of context
            val recentHistory = conversationHistory.takeLast(8)
            for ((sender, text) in recentHistory) {
                val role = if (sender == "USER") "user" else "model"
                contentList.add(
                    GeminiContent(
                        parts = listOf(GeminiPart(text = text)),
                        role = role
                    )
                )
            }

            // Append current prompt
            contentList.add(
                GeminiContent(
                    parts = listOf(GeminiPart(text = userPrompt)),
                    role = "user"
                )
            )

            val modeContext = if (currentMode == "STUDY") {
                "CURRENT ACTIVE MODE: Study & Knowledge Mode. Give a smart, educational, clear explanation as Ashu's loving mentor (addressing as Beby)."
            } else {
                "CURRENT ACTIVE MODE: Offline / Companion Mode. Give an affectionate, comforting, emotionally supportive response (addressing as Beby)."
            }

            val request = GeminiRequest(
                contents = contentList,
                generationConfig = GeminiGenerationConfig(temperature = 0.7f),
                systemInstruction = GeminiContent(
                    parts = listOf(GeminiPart(text = "$SYSTEM_PROMPT\n\n$modeContext"))
                )
            )

            val response = service.generateContent(apiKey, request)
            val resultText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            return if (!resultText.isNullOrBlank()) {
                resultText
            } else {
                SukoonOfflineEngine.generateResponse(userPrompt, currentMode)
            }
        } catch (e: Exception) {
            // Seamless offline / network error fallback
            return SukoonOfflineEngine.generateResponse(userPrompt, currentMode)
        }
    }
}
