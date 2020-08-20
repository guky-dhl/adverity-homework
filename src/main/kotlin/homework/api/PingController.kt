package homework.api

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

class PingController(application: Application) {
    init {
        application.routing {
            get("/ping") {
                call.respond(HttpStatusCode.OK, "OK")
            }
        }
    }
}
