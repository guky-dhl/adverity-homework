package homework.domain

import homework.api.dto.MarketingDataRequest
import homework.api.dto.MarketingDataResponse
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class MarketingReport(val marketingStatistics: MarketingCampaignStatisticRepository) {
    fun by(dataRequest: MarketingDataRequest): MarketingDataResponse {
        return transaction {
            MarketingDataResponse(
                MarketingCampaignStatistic.Table.slice(dataRequest.dimensions.map { it.asExpression() }).select {
                    dataRequest.filters.fold(
                        Op.TRUE as Op<Boolean>,
                        { acc, filter -> acc.and(filter.operation()) })
                }
                    .map { row ->
                        dataRequest.dimensions.map {
                            row[it.asExpression()]!!
                        }.toSet()
                    }
            )
        }
    }
}
