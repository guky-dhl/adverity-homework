package homework.domain

import homework.api.dto.MarketingDataRequest
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class MarketingReport(val marketingStatistics: MarketingCampaignStatisticRepository) {
    fun by(dataRequest: MarketingDataRequest) : List<MarketingCampaignStatistic>{
        return transaction {
            marketingStatistics.find {
                dataRequest.filters.fold(
                    Op.TRUE as Op<Boolean>,
                    { acc, filter -> acc.and(filter.operation()) })
            }.toList()
        }
    }
}
