package net.projecttl.model

import kotlinx.serialization.Serializable

@Serializable
data class MojangAPI(
    val status: Int,
    val unique_id: String,
    val username: String,
    val textures: Textures,
    val res_time: String
)

@Serializable
data class Textures(val skin: Skin, val cape: Cape?)

@Serializable
data class Skin(val url: String)

@Serializable
data class Cape(val url: String)
