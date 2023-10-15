package net.projecttl.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json()
    }
    routing {
        get("/") {
            call.respondRedirect("https://github.com/devproje/project-api.git", true)
        }
    }
}
