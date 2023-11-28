package net.projecttl.papi.utils

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.serialization.json.JsonElement
import net.projecttl.papi.debug

fun unwrapQuote(e: JsonElement?): String {
    return e.toString().replace("\"", "")
}

val client: MongoClient = MongoClient.create(if (debug) {
    "mongodb://localhost:27017"
} else {
    "mongodb://project-api-db:27017"
})

suspend fun database(run: suspend MongoDatabase.() -> Unit = {}) {
    try {
        client.getDatabase("project-api").run()
    } catch (ex: Exception) {
        throw ex
    } finally {
        client.close()
    }

    client.close()
}

suspend inline fun <reified T : Any> database(collName: String, crossinline run: suspend MongoDatabase.(coll: MongoCollection<T>) -> T?): T? {
    val database = client.getDatabase("project-api")

    val data: T? = try {
        database.run(database.getCollection<T>(collName))
    } catch (ex: Exception) {
        throw ex
    } finally {
        client.close()
    }

    return data
}
