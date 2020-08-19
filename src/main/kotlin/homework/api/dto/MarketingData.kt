@file:UseContextualSerialization(Any::class)

package homework.api.dto

import kotlinx.serialization.UseContextualSerialization
import kotlinx.serialization.Serializable

@Serializable
data class MarketingDataRequest(
    val dimensions: Set<Field<*>>,
    val filters: Set<Filter<*>> = setOf(),
    val groupBy: Set<Field<*>> = setOf()
) {
    init {
        dimensions
            .firstOrNull { containsAggregate(it) }
            ?.run {
                val dimensionColumns = dimensions.flatMap { findColumns(it) }
                check(groupBy.containsAll(dimensionColumns))
                {
                    "When aggregate is present group by should contain" +
                            " all dimension columns[$dimensionColumns] " +
                            "what are not aggregated"
                }
            }
    }

    private fun containsAggregate(field: Field<*>): Boolean {
        return when (field) {
            is Field.AggregateField -> true
            is Field.CalculatedField -> containsAggregate(field.first) || containsAggregate(field.second)
            else -> false
        }
    }

    private fun findColumns(field: Field<*>): List<Field<*>> {
        return when (field) {
            is Field.AggregateField -> listOf()
            is Field.CalculatedField -> findColumns(field.first) + findColumns(field.second)
            else -> if (field.isColumn()) listOf(field) else listOf()
        }
    }
}

@Serializable
data class MarketingDataResponse(val result: List<Set<*>>)
