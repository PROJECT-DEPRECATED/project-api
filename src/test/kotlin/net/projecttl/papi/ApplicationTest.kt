package net.projecttl.papi

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import net.projecttl.papi.plugins.configureRouting
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        client.get("/v3").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}
