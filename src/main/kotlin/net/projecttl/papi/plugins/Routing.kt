package net.projecttl.papi.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.projecttl.papi.api.Hangang
import net.projecttl.papi.api.MCProfile
import net.projecttl.papi.api.RoomTemp
import net.projecttl.papi.model.ErrorForm
import net.projecttl.papi.model.HealthCheck
import java.lang.String.format
import kotlin.random.Random

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
            get {
                call.respond(HealthCheck(200, "Hello, World!"))
            }
            get("/hangang") {
                val hangang = Hangang()
                val resp = try {
                    hangang.getData(Random.nextInt(1, 6))
                } catch (ex: Exception) {
                    ErrorForm(500, ex.message!!)
                }

                call.respond(resp)
            }
            route("/hangang") {
                get("/{area}") {
                    val area = call.parameters["area"]!!
                    val hangang = Hangang()
                    val resp = try {
                        hangang.getData(area.toInt())
                    } catch (ex: Exception) {
                        ErrorForm(500, ex.message!!)
                    }

                    call.respond(resp)
                }
            }
            route("/mcprofile") {
                get("/{username}") {
                    val username = call.parameters["username"]!!

                    val profile = try {
                        MCProfile(username).getProfile()
                    } catch (ex: Exception) {
                        ErrorForm(404, format("player `%s` is not found", username))
                    }

                    call.respond(profile)
                }
            }
            route("/room") {
                get("/temp") {
                    val temp = try {
                        RoomTemp().get()
                    } catch (ex: Exception) {
                        // ErrorForm(500, ex.message!!)
                        ErrorForm(404, "Not Found(not created api)")
                    }

                    call.respond(temp)
                }
            }
        }
    }
}
