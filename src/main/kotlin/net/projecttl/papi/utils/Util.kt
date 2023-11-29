package net.projecttl.papi.utils

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.serialization.json.JsonElement
import net.projecttl.papi.debug

val dbURI = if (debug) {
    "mongodb://localhost:27017"
} else {
    "mongodb://project-api-db:27017"
}

fun String.unwrapQuote(): String = this.replace("\"", "")

fun unwrapQuote(e: JsonElement?): String {
    return e.toString().unwrapQuote()
}

suspend fun exec(run: suspend MongoDatabase.() -> Unit = {}) {
    val client = MongoClient.create(dbURI)

    try {
        client.getDatabase("project-api").run()
    } catch (ex: Exception) {
        throw ex
    } finally {
        client.close()
    }
}

inline fun <reified T : Any> query(
    collName: String,
    run: MongoDatabase.(coll: MongoCollection<T>) -> T?
): T? {
    val client = MongoClient.create(dbURI)
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
