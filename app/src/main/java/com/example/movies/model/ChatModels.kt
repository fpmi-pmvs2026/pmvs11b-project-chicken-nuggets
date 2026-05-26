package com.example.movies.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val message: String,
    val favorites: List<String> = emptyList(),
    val count: Int = 5
)

@Serializable
data class ChatResponse(
    val explanation: String,
    val recommendations: List<Recommendation>,
    val follow_up: String? = null
)

@Serializable
data class Recommendation(
    val title: String,
    val reason: String
)

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val recommendations: List<RecommendationWithMovie> = emptyList()
)

data class RecommendationWithMovie(
    val recommendation: Recommendation,
    val movie: Movie? = null
)
