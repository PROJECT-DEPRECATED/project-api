package net.projecttl

import com.mongodb.kotlin.client.coroutine.MongoClient
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import net.projecttl.plugins.configureHTTP
import net.projecttl.plugins.configureRouting

lateinit var client: MongoClient

fun main() {
    database()
    embeddedServer(Netty, port = 3000, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun database() {
    val uri = "mongodb://localhost:27017"
    client = MongoClient.create(uri)
}

fun Application.module() {
    configureHTTP()
    configureRouting()
}
