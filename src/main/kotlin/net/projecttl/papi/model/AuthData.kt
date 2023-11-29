package net.projecttl.papi.model

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

data class User(val uniqueId: String, val name: String) : Principal

@Serializable
data class AuthData(val username: String, val password: String)

data class Account(
    @BsonId val unique_id: String,
    val info: AccountData,
    val wheel: Boolean = false
)

@Serializable
data class AccountData(
    val name: String,
    val email: String,
    val username: String,
    val password: String
)

@Serializable
data class AccountInfo(
    val unique_id: String,
    val name: String,
    val email: String,
    val username: String,
    val wheel: Boolean
)

@Serializable
data class TokenResult(
    val status: Int,
    val token: String
)
