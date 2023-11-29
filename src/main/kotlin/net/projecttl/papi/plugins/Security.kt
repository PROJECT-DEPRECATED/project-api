package net.projecttl.papi.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.projecttl.papi.api.AccountController
import net.projecttl.papi.env
import net.projecttl.papi.model.*
import net.projecttl.papi.model.error.ErrorForm
import net.projecttl.papi.utils.JWTKeygen
import net.projecttl.papi.utils.unwrapQuote

fun Application.configureSecurity() {
    val jwtRealm = env["JWT_REALM"]
    val audience = env["JWT_AUDIENCE"]

    authentication {
        jwt("auth") {
            realm = jwtRealm
            verifier(JWTKeygen.verifier())
            validate { credential ->
                val contain = credential.payload.audience.contains(audience)
                val exist = AccountController.find(
                    credential.payload.getClaim("user_id").asString().unwrapQuote()
                ) != null

                if (contain && exist) JWTPrincipal(credential.payload) else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, ErrorForm(
                    401,
                    "token is not valid or has expired"
                ))
            }
        }
    }

    routing {
        route("/auth") {
            get {
                call.respond(HealthCheck(200, "auth router is work"))
            }
            post("login") {
                val auth = call.receive<AuthData>()
                val authFail = ErrorForm(401, "username or password not match!")
                val login = AccountController(auth).login()
                if (login == null) {
                    call.respond(HttpStatusCode.Unauthorized, authFail)
                    return@post
                }

                val token = JWTKeygen.genToken(login)

                call.respond(TokenResult(200, token))
            }
            put("register") {
                val auth = call.receive<AccountData>()
                if (AccountController(AuthData(auth.username, "")).find() != null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorForm(400, "current username is already exist")
                    )
                    return@put
                }

                AccountController.create(auth)

                call.respond(hashMapOf("status" to 200, "ok" to 1))
            }
            authenticate("auth") {
                get("/refresh") {
                    val principal = call.principal<JWTPrincipal>()!!
                    val payload = principal.payload
                    val user = User(
                        payload.getClaim("user_id").asString(),
                        payload.getClaim("username").asString()
                    )
                    val token = JWTKeygen.genToken(user)

                    call.respond(TokenResult(200, token))
                }
                get("/info") {
                    val principal = call.principal<JWTPrincipal>()!!
                    val payload = principal.payload
                    val acc = AccountController.find(payload.getClaim("user_id").asString())!!
                    val info = AccountInfo(
                        acc.unique_id,
                        acc.info.name,
                        acc.info.email,
                        acc.info.username,
                        acc.wheel
                    )

                    call.respond(info)
                }
            }
        }
    }
}
