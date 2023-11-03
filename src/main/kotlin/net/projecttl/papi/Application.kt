package net.projecttl.papi

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking
import net.projecttl.papi.plugins.configureHTTP
import net.projecttl.papi.plugins.configureRouting
import net.projecttl.papi.utils.database
import kotlin.properties.Delegates

var debug by Delegates.notNull<Boolean>()

fun main(args: Array<String>) {
    debug = args.contains("--debug") || args.contains("-d")
    runBlocking { database() }

    embeddedServer(Netty, port = 3000, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureHTTP()
    configureRouting()
}
