package net.projecttl.papi.model

import io.ktor.server.auth.*
import java.util.UUID

data class SessionData(val uid: UUID, val username: String, val count: Int?) : Principal