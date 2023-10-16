package net.projecttl.api

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import net.projecttl.model.*
import net.projecttl.utils.unwrapQuote
import java.lang.String.format
import java.net.URL
import java.nio.charset.Charset
import java.util.Base64
import java.util.UUID

class MCProfile(private val username: String) {
    private val uuid: UUID
        get() {
            val url = URL(format("https://api.mojang.com/users/profiles/minecraft/%s", username))
            val obj = Json.parseToJsonElement(url.readText(Charset.forName("UTF-8")))
            val rawUUID = unwrapQuote(obj.jsonObject["id"]!!)

            return UUID.fromString(refactorUUID(rawUUID))
        }

    private fun refactorUUID(str: String): String {
        fun String.parse(range: IntRange, end: Boolean = false): String {
            var ps = ""
            for (i in range) {
                ps += this[i]
            }

            if (!end) {
                ps += "-"
            }

            return ps
        }
        var parsed = ""
        parsed += str.parse(0..7)
        parsed += str.parse(8..11)
        parsed += str.parse(12..15)
        parsed += str.parse(16..19)
        parsed += str.parse(20..31, true)

        return parsed
    }

    private fun decode(encoded: String): String {
        return Base64.getDecoder().decode(encoded).decodeToString()
    }

    fun getProfile(): MojangAPI {
        val start = System.currentTimeMillis()
        val url = URL(format("https://sessionserver.mojang.com/session/minecraft/profile/%s", uuid))
        val rawJson = Json.parseToJsonElement(url.readText(Charset.forName("UTF-8")))
        val encoded = unwrapQuote(rawJson.jsonObject["properties"]!!.jsonArray[0].jsonObject["value"]!!)
        val decoded = Json.parseToJsonElement(decode(encoded))
        val cape = try {
            Cape(unwrapQuote(decoded.jsonObject["textures"]!!.jsonObject["CAPE"]!!.jsonObject["url"]!!))
        } catch (ex: Exception) {
            null
        }

        val resTime = System.currentTimeMillis() - start
        return MojangAPI(
            status = 200,
            unique_id = unwrapQuote(decoded.jsonObject["profileId"]!!),
            username = unwrapQuote(decoded.jsonObject["profileName"]!!),
            textures = Textures(
                skin = Skin(
                    url = unwrapQuote(decoded.jsonObject["textures"]!!.jsonObject["SKIN"]!!.jsonObject["url"]!!)
                ),
                cape = cape
            ),
            res_time = "${resTime}ms"
        )
    }
}