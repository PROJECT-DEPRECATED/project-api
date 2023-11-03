package net.projecttl.papi.model

import kotlinx.serialization.Serializable

@Serializable
data class Temperature(val status: Int, val date: String, val time: String, val data: TempData)

@Serializable
data class TempData(val temp: Long, val humidity: Long)
