package net.projecttl.utils

import kotlinx.serialization.json.JsonElement

fun unwrapQuote(e: JsonElement?): String {
    return e.toString().replace("\"", "")
}
