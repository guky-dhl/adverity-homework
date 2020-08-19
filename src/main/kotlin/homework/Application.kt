package homework

import homework.infrastructure.json
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.json
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.KtorExperimentalAPI
import org.koin.core.context.startKoin

fun main(cmdArgs: Array<String>) {
    embeddedServer(
        Netty,
        environment = commandLineEnvironment(cmdArgs)
    ).start(true)
}

@KtorExperimentalAPI
fun Application.main() {
    install(ContentNegotiation) {
        json(
            json = json,
            contentType = ContentType.Application.Json
        )
    }

    startKoin {
        modules(mainModule(this@main), databaseModule)
    }
}
