package homework

import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import org.koin.core.context.startKoin

fun main(cmdArgs: Array<String>) {
    embeddedServer(
        Netty,
        environment = commandLineEnvironment(cmdArgs)
    ).start(true)
}

@KtorExperimentalAPI
fun Application.main() {
    startKoin {
        modules(mainModule(this@main), databaseModule)
    }
}
