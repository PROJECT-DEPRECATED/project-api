package net.projecttl.papi.api

import com.mongodb.client.model.Filters.lt
import kotlinx.coroutines.flow.toList
import net.projecttl.papi.model.input.AccountData
import net.projecttl.papi.model.input.RawAccountData
import net.projecttl.papi.utils.database
import java.security.MessageDigest
import java.util.Base64
import java.util.UUID

class AccountController(private val authData: RawAccountData) {
    suspend fun create(): UUID {
        val uniqueId = UUID.randomUUID()
        val md = MessageDigest.getInstance("SHA-256")
        val encodedPassword = Base64.getEncoder().encodeToString(md.digest(authData.password.toByteArray()))
        val token = Base64.getEncoder().encodeToString(
            md.digest((authData.username + encodedPassword).toByteArray())
        )

        database {
            val coll = getCollection<AccountData>("accounts")
            coll.insertOne(
                AccountData(
                    uniqueId = uniqueId.toString(),
                    name = authData.name,
                    email = authData.email,
                    username = authData.username,
                    password = encodedPassword.toString(),
                    token = token.toString()
                )
            )
        }

        return uniqueId
    }

    companion object {
        suspend fun find(token: String): Boolean {
            var matched = false
            database {
                val coll = getCollection<AccountData>("accounts")
                val accounts = coll.find<AccountData>().toList()
                for (acc in accounts) {
                    if (acc.token == token) {
                        matched = true
                        break
                    }
                }
            }

            return matched
        }
    }
}