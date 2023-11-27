package net.projecttl.papi.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import net.projecttl.papi.api.AccountController
import net.projecttl.papi.env
import net.projecttl.papi.utils.JWTKeygen

fun Application.configureSecurity() {
    val jwtRealm = env["JWT_REALM"]

    authentication {
        jwt("auth") {
            realm = jwtRealm
            verifier(JWTKeygen.verifier())
            validate { credential ->
                if (AccountController.find(credential.payload.getClaim("user_id").asString())) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}
