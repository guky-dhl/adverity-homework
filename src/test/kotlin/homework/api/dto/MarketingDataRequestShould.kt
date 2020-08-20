package homework.api.dto

import homework.domain.*
import homework.infrastructure.json
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class MarketingDataRequestShould {

    @Test
    fun `not be created with aggregate is present by not aggregated field is not in group by `() {
        val error = shouldThrow<IllegalStateException> {
            marketingDataRequest {
                +(sum(MarketingCampaignStatistic::clicks) / selectLong(MarketingCampaignStatistic::impressions))
                +selectString(MarketingCampaignStatistic::dataSource)
            }
        }
        error.message shouldContain "aggregate is present"
        error.message shouldContain "columnName=dataSource"
        error.message shouldContain "columnName=impressions"
    }

    @Test
    fun `be marshaled and un unmurshaled to json`() {
        val request = marketingDataRequest {
            +(sum(MarketingCampaignStatistic::clicks) / selectLong(MarketingCampaignStatistic::impressions))
            +selectString(MarketingCampaignStatistic::dataSource)
            +stringFilter {
                value("")
                column(MarketingCampaignStatistic::dataSource)
            }
            groupBy(
                selectString(MarketingCampaignStatistic::dataSource),
                selectLong(MarketingCampaignStatistic::impressions),
                selectDate(LocalDate.now())
            )
        }
        val message = json.encodeToString(request)

        json.decodeFromString<MarketingDataRequest>(message) shouldBe request
    }

}
