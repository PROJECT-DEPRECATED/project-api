package net.projecttl.papi.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import net.projecttl.papi.api.AccountController
import net.projecttl.papi.env
import net.projecttl.papi.model.AuthData
import net.projecttl.papi.model.error.ErrorForm
import net.projecttl.papi.utils.JWTKeygen

fun Application.configureSecurity() {
    val jwtRealm = env["JWT_REALM"]
    val audience = env["JWT_AUDIENCE"]

    authentication {
        jwt("auth") {
            realm = jwtRealm
            verifier(JWTKeygen.verifier())
            validate { credential ->
                println(credential.payload.getClaim("username"))
                if (credential.payload.audience.contains(audience)
                    && AccountController.exist(credential.payload.getClaim("user_id").asString())) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, ErrorForm(
                    401,
                    "token is not valid or has expired"
                ))
            }
        }
    }
}
