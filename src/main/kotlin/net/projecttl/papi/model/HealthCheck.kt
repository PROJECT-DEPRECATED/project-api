package net.projecttl.papi.model

import kotlinx.serialization.Serializable

@Serializable
data class HealthCheck(
    val status: Int,
    val message: String
)