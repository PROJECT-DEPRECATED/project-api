package net.projecttl.papi.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import net.projecttl.papi.env
import net.projecttl.papi.model.User
import java.util.Date

class JWTKeygen {
    private val validity = 720000 * 10

    private fun getExpired(): Date {
        return Date(System.currentTimeMillis() + validity)
    }

    fun genToken(user: User): String {
        return JWT.create()
            .withSubject("Authentication")
            .withIssuer(issuer)
            .withAudience("papi-user")
            .withClaim("user_id", user.uniqueId)
            .withClaim("username", user.name)
            .withExpiresAt(getExpired())
            .sign(algorithm)
    }

    companion object {
        private val secret = env["JWT_PRIVATE_KEY"]
        private val issuer = env["JWT_ISSUER"]
        private val algorithm = Algorithm.HMAC256(secret)

        fun verifier(): JWTVerifier {
            return JWT.require(algorithm)
                .withIssuer(issuer)
                .build()
        }
    }
}