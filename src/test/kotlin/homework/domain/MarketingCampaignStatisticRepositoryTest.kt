package homework.domain

import homework.infrastructure.RepositoryTest
import homework.infrastructure.TestSubject
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.sql.SchemaUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class MarketingCampaignStatisticRepositoryShould : RepositoryTest() {

    @TestSubject
    val marketingStatistics = MarketingCampaignStatisticRepository()

    @BeforeEach
    internal fun setUp() {
        inTestTransaction {
            SchemaUtils.drop(MarketingCampaignStatistic.Table)
            SchemaUtils.create(MarketingCampaignStatistic.Table)
        }
    }

    @Test
    fun `round trip MarketingCampaignStatistic to database`() {
        val marketingCampaignStatistic =
            MarketingCampaignStatistic("my-datasource", "my-compaing", LocalDate.now(), 1, 1)

        inTestTransaction {
            marketingStatistics.save(marketingCampaignStatistic)
        }

        val statisticsInDb = inTestTransaction {
            marketingStatistics.all().toList()
        }

        statisticsInDb.shouldHaveSize(1)
        statisticsInDb.first().dataSource shouldBe marketingCampaignStatistic.dataSource
        statisticsInDb.first().campaignName shouldBe marketingCampaignStatistic.campaignName
        statisticsInDb.first().at shouldBe marketingCampaignStatistic.at
        statisticsInDb.first().clicks shouldBe marketingCampaignStatistic.clicks
        statisticsInDb.first().impressions shouldBe marketingCampaignStatistic.impressions
    }
}
