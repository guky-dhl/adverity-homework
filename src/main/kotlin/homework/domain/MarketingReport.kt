package homework.domain

import homework.api.dto.field.Field
import homework.api.dto.MarketingDataRequest
import homework.api.dto.MarketingDataResponse
import homework.api.dto.field.SimpleField
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class MarketingReport {
    fun by(dataRequest: MarketingDataRequest): MarketingDataResponse {
        return transaction {
            MarketingDataResponse(
                MarketingCampaignStatistic.Table
                    .slice(selectDimensions(dataRequest))
                    .select(filters(dataRequest))
                    .groupBy(*groupByDimensions(dataRequest))
                    .map(rowToDimensionSet(dataRequest))
            )
        }
    }

    private fun rowToDimensionSet(dataRequest: MarketingDataRequest): (ResultRow) -> Set<SimpleField<*>> {
        return { row ->
            dataRequest.dimensions.map {
                Field.wrapValue(row[it.asExpression()]!!)
            }.toSet()
        }
    }

    private fun filters(dataRequest: MarketingDataRequest): SqlExpressionBuilder.() -> Op<Boolean> {
        return {
            dataRequest.filters.fold(
                Op.TRUE as Op<Boolean>,
                { acc, filter -> acc.and(filter.operation()) })
        }
    }

    private fun selectDimensions(dataRequest: MarketingDataRequest) =
        dataRequest.dimensions.map { it.asExpression() }

    private fun groupByDimensions(dataRequest: MarketingDataRequest): Array<ExpressionWithColumnType<out Any?>> {
        return dataRequest.groupBy
            .map { it.asExpression() }
            .toTypedArray()
    }
}
