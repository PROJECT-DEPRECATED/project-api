package net.projecttl.papi.model

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

data class User(val uniqueId: String, val name: String): Principal

@Serializable
data class AuthData(val username: String, val password: String)

@Serializable
data class Account(
    @BsonId val unique_id: String,
    val info: AccountInfo
)

@Serializable
data class AccountInfo(
    val name: String,
    val email: String,
    val username: String,
    val password: String
)
