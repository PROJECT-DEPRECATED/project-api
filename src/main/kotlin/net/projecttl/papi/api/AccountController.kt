package net.projecttl.papi.api

import com.mongodb.client.model.Filters.eq
import kotlinx.coroutines.flow.singleOrNull
import net.projecttl.papi.model.Account
import net.projecttl.papi.model.AccountData
import net.projecttl.papi.model.AuthData
import net.projecttl.papi.model.User
import net.projecttl.papi.utils.query
import net.projecttl.papi.utils.exec
import java.security.MessageDigest
import java.util.*

class AccountController(private val auth: AuthData) {
    suspend fun find(): Account? {
        val data = try {
            query<Account>(COLL_NAME) {
                it.find(eq("info.username", auth.username)).singleOrNull()
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

        suspend fun find(id: String): Account? {
            val res = try {
                query<Account>(COLL_NAME) {
                    it.find(eq("_id", id)).singleOrNull()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                return null
            }

            return res
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
                exec {
                    val coll = getCollection<Account>(COLL_NAME)
                    coll.insertOne(acc)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}