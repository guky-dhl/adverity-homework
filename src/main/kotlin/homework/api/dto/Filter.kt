@file:ContextualSerialization(LocalDate::class)

package homework.api.dto

import homework.api.dto.Field.*
import homework.api.dto.FilterOperation.*
import homework.domain.MarketingCampaignStatistic
import homework.infrastructure.json
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.`java-time`.dateLiteral
import java.time.LocalDate

@Serializable
sealed class Filter<T : Comparable<T>> {
    abstract val first: Field<T>
    abstract val second: Field<T>?
    abstract val max: Field<T>?
    abstract val valueSet: Set<T>
    abstract val operator: FilterOperation

    companion object {
        val listOperations = setOf(IN, NOT_IN)
    }

    fun operation(): Op<Boolean> = when (operator) {
        in listOperations -> {
            operator(first.asExpression(), valueSet)
        }
        else -> operator(first.asExpression(), second!!.asExpression(), max?.asExpression())
    }

    protected open fun validate() {
        when (operator) {
            in listOperations -> check(valueSet.isNotEmpty()) { "Set of values required if $operator" }
            else -> check(second != null) { "Second field required if filter operator $operator" }
        }
    }

    @Serializable
    @SerialName("StringFilter")
    data class StringFilter(
        override val operator: FilterOperation,
        override val first: StringField,
        override val second: StringField?,
        override val valueSet: Set<String> = setOf()
    ) : Filter<String>() {

        init {
            validate()
        }

        companion object {
            val stringFieldSupportedOperators = setOf(EQ, NE, NOT_IN, IN)
        }

        override fun validate() {
            super.validate()
            check(operator in stringFieldSupportedOperators) {
                "String filter supports only: $stringFieldSupportedOperators"
            }
            check(operator != BETWEEN) {
                "String filter does not support between operations"
            }
        }

        override val max: StringField?
            get() = null
    }

    @Serializable
    @SerialName("LongFilter")
    data class LongFilter(
        override val operator: FilterOperation,
        override val first: LongField,
        override val second: LongField?,
        override val max: Field<Long>? = null,
        override val valueSet: Set<Long>
    ) : Filter<Long>() {
        init {
            validate()
        }
    }

    @Serializable
    @SerialName("DateFilter")
    data class DateFilter(
        override val operator: FilterOperation,
        override val first: DateField,
        override val second: DateField?,
        override val max: DateField? = null,
        override val valueSet: Set<LocalDate> = setOf()
    ) : Filter<LocalDate>() {
        init {
            validate()
        }
    }
}

@Serializable
sealed class Field<T> {
    abstract val columnName: String?
    abstract val value: T?

    protected fun validate() {
        check(hasSingleNotNullParam()) { "Please specify only 1 param" }
    }

    private fun hasSingleNotNullParam() = setOf(columnName, value)
        .count { it != null } == 1


    abstract fun asColumn(): ExpressionWithColumnType<T>
    abstract fun asLiteral(): ExpressionWithColumnType<T>

    fun asExpression(): ExpressionWithColumnType<T> {
        return columnName?.let {
            asColumn()
        } ?: asLiteral()
    }

    @Serializable
    data class StringField(
        override val columnName: String? = null,
        override val value: String? = null
    ) :
        Field<String>() {
        init {
            validate()
        }

        override fun asColumn(): ExpressionWithColumnType<String> {
            val columnFromFieldName = MarketingCampaignStatistic.Table.columnFromFieldName(columnName!!)
            check(columnFromFieldName.columnType is StringColumnType) { "Column[$columnName] is not string" }
            @Suppress("UNCHECKED_CAST")
            return columnFromFieldName as ExpressionWithColumnType<String>
        }

        override fun asLiteral(): ExpressionWithColumnType<String> = stringLiteral(value!!)
    }

    @Serializable
    data class DateField(
        override val columnName: String? = null,
        override val value: @ContextualSerialization LocalDate? = null
    ) :
        Field<LocalDate>() {
        init {
            validate()
        }

        override fun asColumn(): ExpressionWithColumnType<LocalDate> {
            val columnFromFieldName = MarketingCampaignStatistic.Table.columnFromFieldName(columnName!!)
            check(columnFromFieldName.columnType is IDateColumnType) { "Column[$columnName] is not date type" }
            @Suppress("UNCHECKED_CAST")
            return columnFromFieldName as ExpressionWithColumnType<LocalDate>
        }

        override fun asLiteral(): ExpressionWithColumnType<LocalDate> = dateLiteral(value!!)
    }

    @Serializable
    data class LongField(
        override val columnName: String? = null,
        override val value: Long? = null
    ) :
        Field<Long>() {
        init {
            validate()
        }

        override fun asColumn(): ExpressionWithColumnType<Long> {
            val columnFromFieldName = MarketingCampaignStatistic.Table.columnFromFieldName(columnName!!)
            check(columnFromFieldName.columnType is LongColumnType) { "Column[$columnName] is not long type" }
            @Suppress("UNCHECKED_CAST")
            return columnFromFieldName as ExpressionWithColumnType<Long>
        }

        override fun asLiteral(): ExpressionWithColumnType<Long> = longLiteral(value!!)
    }
}
