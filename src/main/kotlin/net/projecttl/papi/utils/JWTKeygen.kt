package net.projecttl.papi.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import net.projecttl.papi.env
import net.projecttl.papi.model.User
import java.util.*

object JWTKeygen {
    private val secret = env["JWT_PRIVATE_KEY"]
    private val issuer = env["JWT_ISSUER"]
    private val audience = env["JWT_AUDIENCE"]
    private val algorithm = Algorithm.HMAC256(secret)

    private const val VALIDITY = 7200000

    private fun getExpired(): Date {
        return Date(System.currentTimeMillis() + VALIDITY)
    }

    fun verifier(): JWTVerifier {
        return JWT.require(algorithm)
            .withIssuer(issuer)
            .build()
    }

    fun genToken(user: User): String {
        return JWT.create()
            .withSubject("Project API Authentication")
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("user_id", user.uniqueId)
            .withClaim("username", user.name)
            .withExpiresAt(getExpired())
            .sign(algorithm)
    }
}