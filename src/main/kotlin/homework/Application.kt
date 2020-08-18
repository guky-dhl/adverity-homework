package homework

import io.ktor.application.Application
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
    startKoin {
        // use Koin logger
        printLogger()
        // declare modules
        modules(mainModule(this@main))
    }

}
