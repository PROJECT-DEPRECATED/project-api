package net.projecttl.papi.utils

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.serialization.json.JsonElement
import net.projecttl.papi.debug

fun unwrapQuote(e: JsonElement?): String {
    return e.toString().replace("\"", "")
}

suspend fun database(run: suspend MongoDatabase.() -> Unit = {}) {
    val uri = if (debug) "mongodb://localhost:27017" else "mongo://project-api-db:27017"
    val client = MongoClient.create(uri)

    try {
        client.getDatabase("project-api").run()
    } catch (ex: Exception) {
        ex.printStackTrace()
    } finally {
        client.close()
    }
}
