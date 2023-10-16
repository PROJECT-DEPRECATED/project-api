package net.projecttl.api

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import net.projecttl.model.HData
import net.projecttl.model.HangangData
import net.projecttl.utils.unwrapQuote
import java.lang.String.format
import java.net.URL
import java.nio.charset.Charset

class Hangang {
    private val rawURL = "http://openapi.seoul.go.kr:8088/sample/json/WPOSInformationTime"

    fun getData(area: Int): HangangData {
        val start = System.currentTimeMillis()
        val url = URL(format("%s/%d/%d", rawURL, area, area))
        val obj = Json.parseToJsonElement(url.readText(Charset.forName("UTF-8")))
        val data = obj.jsonObject["WPOSInformationTime"]!!.jsonObject["row"]!!.jsonArray[0]

        val resTime = System.currentTimeMillis() - start
        return HangangData(
            status = 200,
            area = unwrapQuote(data.jsonObject["SITE_ID"]),
            area_code = area,
            data = HData(
                ph = unwrapQuote(data.jsonObject["W_PH"]),
                temp = unwrapQuote(data.jsonObject["W_TEMP"]),
                date = unwrapQuote(data.jsonObject["MSR_DATE"]),
                time = unwrapQuote(data.jsonObject["MSR_TIME"])
            ),
            res_time = "${resTime}ms"
        )
    }
}