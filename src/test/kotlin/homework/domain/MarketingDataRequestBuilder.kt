package homework.domain

import homework.api.dto.Field.DateField
import homework.api.dto.Field.StringField
import homework.api.dto.Filter
import homework.api.dto.Filter.DateFilter
import homework.api.dto.Filter.StringFilter
import homework.api.dto.FilterOperation
import homework.api.dto.FilterOperation.BETWEEN
import homework.api.dto.MarketingDataRequest
import java.time.LocalDate
import kotlin.reflect.KProperty1

class MarketingDataRequestBuilder {
    private var filters: MutableSet<Filter<*>> = mutableSetOf()

    operator fun Filter<*>.unaryPlus() {
        filters.add(this)
    }

    fun build(init: MarketingDataRequestBuilder.() -> Unit): MarketingDataRequest {
        this.init()
        return MarketingDataRequest(filters)
    }
}

fun marketingDataRequest(init: MarketingDataRequestBuilder.() -> Unit = {}) = MarketingDataRequestBuilder().build(init)

class StringFilterBuilder {
    var operation = FilterOperation.EQ
    private val operands = mutableListOf<StringField>()
    private val inValues = mutableListOf<String>()

    fun value(value: String) = operands.add(StringField(value = value))
    fun column(property: KProperty1<MarketingCampaignStatistic, String>) = operands.add(StringField(property.name))
    fun values(vararg values: String) = inValues.addAll(values)

    fun build(init: StringFilterBuilder.() -> Unit): StringFilter {
        this.init()
        check(operands.size == 2 || operands.size == 1 && inValues.size > 0) { "Filter accepts exactly 2 operands or 1 operand and values" }
        return StringFilter(operation, operands[0], operands.elementAtOrNull(1), inValues.toSet())
    }
}

fun stringFilter(init: StringFilterBuilder.() -> Unit): StringFilter = StringFilterBuilder().build(init)

class DateFilterBuilder {
    var operation = FilterOperation.EQ
    private val operands = mutableListOf<DateField>()
    private val inValues = mutableListOf<LocalDate>()

    fun value(value: LocalDate) = operands.add(DateField(value = value))
    fun column(property: KProperty1<MarketingCampaignStatistic, LocalDate>) = operands.add(DateField(property.name))
    fun values(vararg values: LocalDate) = inValues.addAll(values)

    fun build(init: DateFilterBuilder.() -> Unit): DateFilter {
        this.init()
        check(
            (operands.size == 3 && operation == BETWEEN)
                    || (operands.size == 2 && operation != BETWEEN)
                    || (operands.size == 1 && inValues.size > 0)
        ) { "Filter accepts exactly 2 operands or 1 operand and values" }
        return DateFilter(
            operation,
            operands[0],
            operands.elementAtOrNull(1),
            operands.elementAtOrNull(2),
            inValues.toSet()
        )
    }
}

fun dateFilter(init: DateFilterBuilder.() -> Unit): DateFilter = DateFilterBuilder().build(init)
