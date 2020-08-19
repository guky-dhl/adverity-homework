package homework.api

import homework.domain.MarketingReport
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing

class MarketingStatisticController(
    application: Application,
    report: MarketingReport
) {
    init {
        application.routing {
            post("/marketing-data") {
                call.respond(HttpStatusCode.OK, report.by(call.receive()))
            }
        }
    }
}
