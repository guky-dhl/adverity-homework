@file:UseContextualSerialization(Any::class)

package homework.api.dto

import homework.api.dto.field.AggregateField
import homework.api.dto.field.CalculatedField
import homework.api.dto.field.Field
import homework.api.dto.field.SimpleField
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseContextualSerialization

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
            is AggregateField -> true
            is CalculatedField -> containsAggregate(field.first) || containsAggregate(field.second)
            else -> false
        }
    }

    private fun findColumns(field: Field<*>): List<Field<*>> {
        return when (field) {
            is AggregateField -> listOf()
            is CalculatedField -> findColumns(field.first) + findColumns(field.second)
            else -> if (field.isColumn()) listOf(field) else listOf()
        }
    }
}

@Serializable
data class MarketingDataResponse(val result: List<Set<SimpleField<*>>>)
