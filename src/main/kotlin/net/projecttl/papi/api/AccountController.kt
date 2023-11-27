package net.projecttl.papi.api

import com.mongodb.client.model.Filters.eq
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.coroutines.flow.singleOrNull
import net.projecttl.papi.model.Account
import net.projecttl.papi.model.AccountInfo
import net.projecttl.papi.model.AuthData
import net.projecttl.papi.model.User
import net.projecttl.papi.utils.database
import java.lang.NullPointerException
import java.security.MessageDigest
import java.util.*

class AccountController(private val info: AccountInfo) {
    suspend fun create() {
        val id = UUID.randomUUID().toString()
        val hashedPassword = hash(info.password, hash(id))

        val newInfo = AccountInfo(
            info.name,
            info.email,
            info.username,
            hashedPassword
        )

        val acc = Account(id, newInfo)
        database {
            val coll = getCollection<Account>("accounts")
            coll.insertOne(acc)
        }
    }

    companion object {
        fun hash(str: String): String {
            val md = MessageDigest.getInstance("SHA-256")
            return Base64.getEncoder()
                .encodeToString(md.digest(str.toByteArray()))
        }

        fun hash(str: String, salt: String): String {
            return hash("$str:$salt")
        }

        private suspend fun find(auth: AuthData): Account? {
            var data: Account? = null
            database {
                val coll = getCollection<Account>("accounts")
                data = coll.find(eq("info.username", auth.username)).singleOrNull()
            }

            return data
        }

        suspend fun find(id: String): Boolean {
            var data: Account? = null
            database {
                val coll = getCollection<Account>("accounts")
                data = coll.find(eq("unique_id", id)).singleOrNull()
            }

            return data != null
        }

        suspend fun login(auth: AuthData): User? {
            val data = find(auth) ?: return null

            if (hash(auth.password, hash(data.unique_id)) != data.info.password) {
                return null
            }

            return User(data.unique_id, data.info.username)
        }
    }
}