package net.projecttl.api

import io.ktor.utils.io.charsets.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import net.projecttl.model.HData
import net.projecttl.model.HangangData
import java.net.URL
import java.nio.charset.Charset

class Hangang {
    private val RAW_URL = "http://openapi.seoul.go.kr:8088/sample/json/WPOSInformationTime"
    fun getData(area: Int = 2): HangangData {
        val start = System.currentTimeMillis()
        val url = URL("$RAW_URL/$area/$area")
        val obj = Json.parseToJsonElement(url.readText(Charset.forName("UTF-8")))
        val data = obj.jsonObject["WPOSInformationTime"]!!.jsonObject["row"]!!.jsonArray[0]

        val resTime = System.currentTimeMillis() - start
        return HangangData(
            status = 200,
            area = data.jsonObject["SITE_ID"].toString().replace("\"", ""),
            data = HData(
                ph = data.jsonObject["W_PH"].toString().replace("\"", ""),
                temp = data.jsonObject["W_TEMP"].toString().replace("\"", ""),
                date = data.jsonObject["MSR_DATE"].toString().replace("\"", ""),
                time = data.jsonObject["MSR_TIME"].toString().replace("\"", "")
            ),
            res_time = "${resTime}ms"
        )
    }
}