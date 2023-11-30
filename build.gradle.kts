val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val mongo_driver_version: String by project

data class Deps(val name: String, val jvm: Boolean = true, val testImpl: Boolean = false)

val deps = listOf(
    Deps("server-auth"),
    Deps("server-cors"),
    Deps("server-core"),
    Deps("server-netty"),
    Deps("server-swagger"),
    Deps("server-content-negotiation"),
    Deps("serialization-kotlinx-json"),
    Deps("server-sessions", jvm = false),
    Deps("server-auth-jwt", jvm = false),
    Deps("server-tests", testImpl = true)
)

plugins {
    kotlin("jvm") version "1.9.21"
    id("io.ktor.plugin") version "2.3.6"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10"
}

group = property("group")!!
version = property("version")!!

application {
    mainClass.set("net.projecttl.papi.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    for (dep in deps) {
        var str = "io.ktor:ktor-${dep.name}"
        if (dep.jvm) {
            str += "-jvm"
        }

        if (dep.testImpl) {
            testImplementation(str)
            continue
        }

        implementation(str)
    }

    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:$mongo_driver_version")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

tasks {
    processResources {
        filesMatching("version.txt") {
            expand(project.properties)
        }
    }
}
