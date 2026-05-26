package com.example.movies.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerResponse(
    @SerialName("iframe")
    val iframe: String,
    @SerialName("name")
    val name: String
)