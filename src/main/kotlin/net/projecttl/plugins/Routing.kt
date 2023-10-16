package net.projecttl.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.projecttl.api.Hangang
import net.projecttl.model.ErrorForm

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json()
    }
    routing {
        staticResources("/static", "public") {
            enableAutoHeadResponse()
        }
        get("/") {
            call.respondRedirect("https://github.com/devproje/project-api.git", true)
        }
        route("/v3") {
            get("/hangang") {
                val hangang = Hangang()
                try {
                    hangang.getData()
                } catch (ex: Exception) {
                    call.respond(ErrorForm(
                        500,
                        ex.message!!
                    ))
                }
                call.respond(hangang.getData())
            }
            get("/mcprofile") {}
        }
    }
}
