package homework.api

import homework.api.dto.Field.SimpleField.DateField
import homework.api.dto.FilterOperation.BETWEEN
import homework.api.dto.FilterOperation.EQ
import homework.api.dto.MarketingDataResponse
import homework.domain.*
import homework.infrastructure.ApiTest
import homework.infrastructure.asDecimal
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.koin.core.context.stopKoin
import org.koin.core.inject
import java.time.LocalDate
import java.time.LocalDate.parse

@TestInstance(PER_CLASS)
internal class MarketingStatisticControllerShould : ApiTest() {

    val client: HttpClient by inject()
    val randomDate = parse("2019-11-12")

    @Test
    fun `Report total Clicks for a given Datasource for a given Date range`() {
        val marketingDataRequest = marketingDataRequest {
            +sum(MarketingCampaignStatistic::clicks)
            +stringFilter {
                column(MarketingCampaignStatistic::dataSource)
                operation = EQ
                value("Twitter Ads")
            }
            +dateFilter {
                column(MarketingCampaignStatistic::at)
                operation = BETWEEN
                value(randomDate)
                value(randomDate.plusDays(2))
            }
        }
        val response = runBlocking {
            client.post<MarketingDataResponse> {
                url {
                    encodedPath = "/marketing-data"
                }
                body = marketingDataRequest
            }
        }

        response.result.shouldHaveSize(1)
        response.result.single().single().value shouldBe 25784.asDecimal()
    }

    @Test
    fun `Report click-through Rate (CTR) per Datasource and Campaign`() {
        //CTR = (clicks / impressions) * 100
        val marketingDataRequest = marketingDataRequest {
            +((sum(MarketingCampaignStatistic::clicks) / sum(MarketingCampaignStatistic::impressions)) * 100)
            +stringFilter {
                column(MarketingCampaignStatistic::dataSource)
                operation = EQ
                value("Twitter Ads")
            }
            +stringFilter {
                column(MarketingCampaignStatistic::campaignName)
                operation = EQ
                value("Adventmarkt Touristik")
            }
        }
        val response = runBlocking {
            client.post<MarketingDataResponse> {
                url {
                    encodedPath = "/marketing-data"
                }
                body = marketingDataRequest
            }
        }

        response.result.shouldHaveSize(1)
        response.result.single().single().value shouldBe 1.2554.asDecimal()
    }

    @Test
    fun `Impressions over time (daily)`() {
        val marketingDataRequest = marketingDataRequest {
            +sum(MarketingCampaignStatistic::impressions)
            +selectDate(MarketingCampaignStatistic::at)
            groupBy(selectDate(MarketingCampaignStatistic::at))
        }
        val response = runBlocking {
            client.post<MarketingDataResponse> {
                url {
                    encodedPath = "/marketing-data"
                }
                body = marketingDataRequest
            }
        }

        response.result.shouldHaveSize(410)
        val recordAtRandomDate =
            response.result.single { (it.single { it is DateField }.value as LocalDate) == randomDate }
        recordAtRandomDate.shouldHaveSize(2)
        recordAtRandomDate.first().value shouldBe 275228.asDecimal()
        recordAtRandomDate.last().value shouldBe randomDate
    }

    companion object {
        @AfterAll
        internal fun tearDown() {
            stopKoin()
        }
    }
}
