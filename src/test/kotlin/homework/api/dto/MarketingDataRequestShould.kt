package homework.api.dto

import homework.domain.MarketingCampaignStatistic
import homework.domain.marketingDataRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test

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

}
