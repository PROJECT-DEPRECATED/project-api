package net.projecttl.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun Application.configureOpenAPI() {
    routing {
        swaggerUI("/swagger", "openapi/documentation.yaml")
    }
}
