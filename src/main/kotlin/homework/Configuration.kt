package homework

import homework.api.PingController
import homework.domain.MarketingCampaignStatisticRepository
import io.ktor.application.Application
import org.koin.dsl.module

fun mainModule(application: Application) = module(createdAtStart = true) {
    single { application }
    single { application.environment }
    single { MarketingCampaignStatisticRepository() }
    single { PingController(get()) }
}
