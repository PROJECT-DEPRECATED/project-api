package net.projecttl.papi.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import net.projecttl.papi.env
import net.projecttl.papi.utils.JWTKeygen

fun Application.configureSecurity() {
    val jwtRealm = env["JWT_REALM"]
    val audience = env["JWT_AUDIENCE"]

    authentication {
        jwt("auth") {
            realm = jwtRealm
            verifier(JWTKeygen.verifier())
            validate { credential ->
                if (credential.payload.audience.contains(audience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "token is not valid or has expired")
            }
        }
    }
}
