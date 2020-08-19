package homework.domain

import homework.api.dto.Field
import homework.api.dto.Field.*
import homework.api.dto.Field.AggregateField.AggregationType.*
import homework.api.dto.Field.CalculatedField.CalculationType.DIVIDE
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
    private var dimensions: MutableSet<Field<*>> =
        mutableSetOf()
    private var groupBy: MutableSet<Field<*>> =
        mutableSetOf()

    operator fun Filter<*>.unaryPlus() {
        filters.add(this)
    }

    operator fun Field<*>.unaryPlus() {
        dimensions.add(this)
    }

    operator fun Field<Long>.div(second: Field<Long>): Field<Long> {
        return CalculatedField(DIVIDE, this, second)
    }

    fun groupBy(vararg field: Field<*>) {
        groupBy.addAll(field)
    }

    fun selectString(property: KProperty1<MarketingCampaignStatistic, String>): StringField = StringField(property.name)

    fun selectDate(property: KProperty1<MarketingCampaignStatistic, LocalDate>): DateField = DateField(property.name)

    fun selectLong(property: KProperty1<MarketingCampaignStatistic, Long>): LongField = LongField(property.name)

    fun selectLong(value: Long): LongField = LongField(value = value)

    fun sum(property: KProperty1<MarketingCampaignStatistic, Long>): AggregateField =
        AggregateField(LongField(property.name), SUM)

    fun count(property: KProperty1<MarketingCampaignStatistic, Long>): AggregateField =
        AggregateField(LongField(property.name), COUNT)

    fun div(property: KProperty1<MarketingCampaignStatistic, Long>): Boolean {
        return dimensions.add(AggregateField(LongField(property.name), COUNT))
    }

    fun avg(property: KProperty1<MarketingCampaignStatistic, Long>) = AggregateField(LongField(property.name), AVERAGE)

    fun build(init: MarketingDataRequestBuilder.() -> Unit): MarketingDataRequest {
        this.init()
        if (dimensions.isEmpty()) {
            dimensions.add(StringField(MarketingCampaignStatistic::dataSource.name))
        }
        return MarketingDataRequest(dimensions, filters, groupBy)
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
