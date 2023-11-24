package net.projecttl.papi.model.input

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
data class Credential(val username: String, val password: String)

@Serializable
data class RawAccountData(
    val name: String,
    val email: String,
    val username: String,
    val password: String
)

data class AccountData(
    @BsonId val uniqueId: String,
    val name: String,
    val email: String,
    val username: String,
    val password: String,
    val token: String
)
