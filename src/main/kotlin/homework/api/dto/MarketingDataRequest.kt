@file:ContextualSerialization(Any::class)

package homework.api.dto

import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable

@Serializable
data class MarketingDataRequest(val filters: Set<Filter<*>>)

