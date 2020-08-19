@file:UseContextualSerialization(LocalDate::class)

package homework.api.dto

import homework.api.dto.Field.*
import homework.api.dto.FilterOperation.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseContextualSerialization
import org.jetbrains.exposed.sql.Op
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