package net.projecttl.papi.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.projecttl.papi.api.AccountController
import net.projecttl.papi.api.Hangang
import net.projecttl.papi.api.MCProfile
import net.projecttl.papi.model.AccountInfo
import net.projecttl.papi.model.AuthData
import net.projecttl.papi.model.error.ErrorForm
import net.projecttl.papi.model.HealthCheck
import net.projecttl.papi.model.User
import net.projecttl.papi.utils.JWTKeygen
import java.lang.String.format
import kotlin.random.Random

fun Application.configureRouting() {
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
                        ErrorForm(404, format("player '%s' is not found", username))
                    }

                    call.respond(profile)
                }
            }
        }
        route("/auth") {
            post("login") {
                val auth = call.receive<AuthData>()
                val authFail = ErrorForm(401, "username or password not match!")
                val login = AccountController.login(auth)
                if (login == null) {
                    call.respond(authFail)
                    return@post
                }

                val token = JWTKeygen().genToken(login)

                call.respond(hashMapOf("token" to token))
            }
            put("register") {
                val auth = call.receive<AccountInfo>()
                AccountController(auth).create()
            }
            authenticate("auth") {
                get("refresh") {
                    val old = call.response.headers["token"]
                    println(old)

//                    val token = JWTKeygen().genToken()
                }
            }
        }
    }
}
