package homework.infrastructure

import io.ktor.application.ApplicationEnvironment
import io.ktor.config.ApplicationConfig
import io.ktor.util.KtorExperimentalAPI

@OptIn(KtorExperimentalAPI::class)
val ApplicationEnvironment.envKind
    get() = this.config.property("ktor.application.env").getString()

val ApplicationEnvironment.isDev
    get() = envKind == "dev"

val ApplicationEnvironment.isProd
    get() = envKind == "prod"

val ApplicationEnvironment.isTest
    get() = envKind == "test"

@OptIn(KtorExperimentalAPI::class)
internal operator fun ApplicationConfig.get(s: String): String {
    return this.property(s).getString()
}


