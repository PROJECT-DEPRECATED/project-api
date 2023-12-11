package net.projecttl.papi

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import net.projecttl.papi.plugins.configureHTTP
import net.projecttl.papi.plugins.configureRouting
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
            configureHTTP()
        }
        client.get("/v3").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}
