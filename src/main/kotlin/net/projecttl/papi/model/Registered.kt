package net.projecttl.papi.model

import kotlinx.serialization.Serializable

@Serializable
data class Registered(
    val status: Int,
    val uniqueId: String,
    val username: String,
    val ok: Int = 1
)