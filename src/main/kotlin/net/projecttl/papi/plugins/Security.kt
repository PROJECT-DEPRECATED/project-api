package net.projecttl.papi.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import net.projecttl.papi.api.AccountController

fun Application.configureSecurity() {
    authentication {
        bearer("pauth") {
            realm = "Project_IO's Personal API Server"
            authenticate { credential ->
                if (AccountController.find(credential.token)) {
                    return@authenticate UserIdPrincipal("test")
                }

                null
            }
        }
    }
}
