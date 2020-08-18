package homework

import homework.api.PingController
import homework.domain.LoadStatistic
import homework.domain.MarketingCampaignStatistic
import homework.domain.MarketingCampaignStatisticRepository
import homework.infrastructure.dropCreate
import homework.infrastructure.get
import homework.infrastructure.isProd
import io.ktor.application.Application
import io.ktor.application.ApplicationEnvironment
import io.ktor.util.KtorExperimentalAPI
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.dsl.module

fun mainModule(application: Application) = module(createdAtStart = true) {
    single { application }
    single { application.environment }
    single { MarketingCampaignStatisticRepository() }
    single { PingController(get(), get()) }
    single { LoadStatistic(get()) }
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
