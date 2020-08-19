package homework.api

import homework.api.dto.Filter
import homework.api.dto.MarketingDataRequest
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import org.jetbrains.exposed.sql.Database

class PingController(application: Application, db: Database) {
    init {
        application.routing {
            get("/ping") {
                call.respond(HttpStatusCode.OK, "OK")
            }
        }
    }
}
