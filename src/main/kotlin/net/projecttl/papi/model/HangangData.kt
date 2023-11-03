package net.projecttl.papi.model

import kotlinx.serialization.Serializable

@Serializable
data class HangangData(
    val status: Int,
    val area: String,
    val area_code: Int,
    val data: HData,
    val res_time: String
)

@Serializable
data class HData(
    val ph: String,
    val temp: String,
    val date: String,
    val time: String,
)
