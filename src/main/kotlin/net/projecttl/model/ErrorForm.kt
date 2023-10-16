package net.projecttl.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorForm(
    val status: Int,
    val reason: String
)