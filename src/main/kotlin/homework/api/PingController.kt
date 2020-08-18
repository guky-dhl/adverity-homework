package homework.api

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing

class PingController(application: Application) {
    init {
        application.routing {
            get("/ping") {
                call.respond(HttpStatusCode.OK, "OK")
            }
        }
    }
}
