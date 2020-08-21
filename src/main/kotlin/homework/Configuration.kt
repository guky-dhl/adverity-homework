package homework

import homework.api.MarketingStatisticController
import homework.api.PingController
import homework.api.dto.ErrorResponse
import homework.domain.LoadStatistic
import homework.domain.MarketingCampaignStatistic
import homework.domain.MarketingCampaignStatisticRepository
import homework.domain.MarketingReport
import homework.infrastructure.dropCreate
import homework.infrastructure.get
import homework.infrastructure.isProd
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.serialization.*
import io.ktor.util.KtorExperimentalAPI
import kotlinx.serialization.SerializationException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.dsl.module
import org.slf4j.event.Level

fun mainModule(application: Application) = module(createdAtStart = true) {
    installJson(application)
    installErrorHandling(application)
    installCallLogging(application)
    single { application }
    single { application.environment }
    single { MarketingCampaignStatisticRepository() }
    single { PingController(get()) }
    single { LoadStatistic(get(), get()) }
    single { MarketingStatisticController(get(), get()) }
    single { MarketingReport() }
}

private fun installJson(application: Application) {
    application.install(ContentNegotiation) {
        json(
            json = homework.infrastructure.json,
            contentType = ContentType.Application.Json
        )
    }
}

private fun installErrorHandling(application: Application) {
    application.install(StatusPages) {
        exception<SerializationException> { cause ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(cause.message))
        }
        exception<Exception> { cause ->
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse(cause.message))
        }
    }

}

private fun installCallLogging(application: Application) {
    application.install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/marketing-data") }
    }


}

@OptIn(KtorExperimentalAPI::class)
val databaseModule = module {
    single {
        val env = get<ApplicationEnvironment>()
        val dbConfig = env.config.config("db")

        val database = if (env.isProd) {
            Database.Companion.connect(
                url = "jdbc:postgresql://${dbConfig["host"]}:${dbConfig["port"]}/${dbConfig["database-name"]}",
                user = dbConfig["user"],
                password = dbConfig["password"],
                driver = "org.postgresql.Driver"
            )
        } else {
            Database.connect("jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;", "org.h2.Driver")
        }

        recreateTables()

        database
    }
}

private fun recreateTables() {
    transaction {
        SchemaUtils.dropCreate(MarketingCampaignStatistic.Table)
    }
}
