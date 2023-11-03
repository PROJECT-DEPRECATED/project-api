package net.projecttl.papi.api

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import net.projecttl.papi.model.TempData
import net.projecttl.papi.model.Temperature
import net.projecttl.papi.utils.unwrapQuote
import java.net.URL
import java.nio.charset.Charset

class RoomTemp {
    private val posURL = "" // Not modified

    fun get(): Temperature {
        val url = URL(posURL).readText(Charset.forName("UTF-8"))
        val data = Json.parseToJsonElement(url)

        return Temperature(
            status = 200,
            date = unwrapQuote(data.jsonObject["date"]!!),
            time = unwrapQuote(data.jsonObject["time"]!!),
            data = TempData(
                temp = unwrapQuote(data.jsonObject["temp"]!!).toLong(),
                humidity = unwrapQuote(data.jsonObject["humidity"]!!).toLong()
            )
        )
    }
}