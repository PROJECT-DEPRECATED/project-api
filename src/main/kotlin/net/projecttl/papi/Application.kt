package net.projecttl.papi

import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import net.projecttl.papi.plugins.configureHTTP
import net.projecttl.papi.plugins.configureRouting
import net.projecttl.papi.plugins.configureSecurity
import net.projecttl.papi.utils.exec

val debug = System.getProperty("io.ktor.development").toBoolean()
val env = dotenv()

fun main() {
    try {
        exec()
    } catch (ex: Exception) {
        ex.printStackTrace()
        return
    }

    embeddedServer(Netty, port = 3000, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSecurity()
    configureRouting()
    configureHTTP()
}
