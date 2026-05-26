package com.example.movies.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExternalIdResponse(
    val docs: List<ExternalId>
)

@Serializable
data class ExternalId(
    @SerialName("id")
    val id: Int
)