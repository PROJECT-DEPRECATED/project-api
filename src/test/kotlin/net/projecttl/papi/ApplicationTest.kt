package net.projecttl.papi

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import net.projecttl.papi.plugins.configureHTTP
import net.projecttl.papi.plugins.configureRouting
import kotlin.test.Test
import kotlin.test.assertEquals

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

    @Test
    fun testHangang() = testApplication {
        application {
            configureRouting()
            configureHTTP()
        }
        client.get("/v3/hangang").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}
