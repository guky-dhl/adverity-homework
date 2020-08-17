package homework

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.KtorExperimentalAPI


fun main(cmdArgs: Array<String>) {
    embeddedServer(
        Netty,
        environment = commandLineEnvironment(cmdArgs)
    ).start(true)
}

@KtorExperimentalAPI
fun Application.main() {
    routing {
        get("/ping") {
            call.respond(HttpStatusCode.OK, "test-stream")
        }
    }
}
