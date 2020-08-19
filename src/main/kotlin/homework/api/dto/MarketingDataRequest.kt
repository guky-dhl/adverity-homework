@file:ContextualSerialization(Any::class)

package homework.api.dto

import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable

@Serializable
data class MarketingDataRequest(val dimensions: Set<Field<*>>, val filters: Set<Filter<*>>)

@Serializable
data class MarketingDataResponse(val result: List<Set<*>>)
