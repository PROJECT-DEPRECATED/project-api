package net.projecttl.papi.api

import com.mongodb.client.model.Filters.eq
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.runBlocking
import net.projecttl.papi.model.Account
import net.projecttl.papi.model.AccountData
import net.projecttl.papi.model.AuthData
import net.projecttl.papi.model.User
import net.projecttl.papi.utils.query
import net.projecttl.papi.utils.modify
import java.security.MessageDigest
import java.util.*
import kotlin.NullPointerException

enum class AccountDataType {
    NAME,
    EMAIL,
    PASSWORD
}

class AccountController {
    private val auth: AuthData
    private val id: UUID

    constructor(auth: AuthData) {
        val data = runBlocking { find() ?: throw NullPointerException() }

        this.auth = auth
        this.id = UUID.fromString(data.unique_id)
    }

    constructor(id: UUID) {
        val data = runBlocking { find() ?: throw NullPointerException() }

        this.id = id
        this.auth = AuthData(data.info.username, data.info.password)
    }

    suspend fun find(): Account? {
        val data = try {
            query<Account>(COLL_NAME) {
                it.find(eq("_id", id.toString())).singleOrNull()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }

        return data
    }

    suspend fun login(): User? {
        val data = find() ?: return null
        if (hash(auth.password, hash(data.unique_id)) != data.info.password) {
            return null
        }

        return User(data.unique_id, data.info.username)
    }

    suspend fun edit(type: AccountDataType, value: String) {
        val item = when (type) {
            AccountDataType.NAME -> "info.name"
            AccountDataType.EMAIL -> "info.email"
            AccountDataType.PASSWORD -> "info.password"
        }

        modify<Account>(COLL_NAME) {
            it.updateOne(eq("_id", id.toString()), eq("\$set", eq(item, value)))
        }
    }

    companion object {
        private const val COLL_NAME = "accounts"

        private fun hash(str: String): String {
            val md = MessageDigest.getInstance("SHA-256")
            return Base64.getEncoder()
                .encodeToString(md.digest(str.toByteArray()))
        }

        private fun hash(str: String, salt: String): String {
            return hash("$str:$salt")
        }

        suspend fun create(info: AccountData) {
            val id = UUID.randomUUID().toString()
            val hashedPassword = hash(info.password, hash(id))

            val newInfo = AccountData(
                info.name,
                info.email,
                info.username,
                hashedPassword
            )

            try {
                val acc = Account(id, newInfo)
                modify<Account>(COLL_NAME) {
                    it.insertOne(acc)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}