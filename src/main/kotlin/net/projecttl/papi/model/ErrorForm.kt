package net.projecttl.papi.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorForm(
    val status: Int,
    val reason: String
)