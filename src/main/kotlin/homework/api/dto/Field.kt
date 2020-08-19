@file:UseContextualSerialization(LocalDate::class)

package homework.api.dto

import homework.domain.MarketingCampaignStatistic
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseContextualSerialization
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import org.jetbrains.exposed.sql.SqlExpressionBuilder.div
import org.jetbrains.exposed.sql.SqlExpressionBuilder.times
import org.jetbrains.exposed.sql.`java-time`.dateLiteral
import java.time.LocalDate

@Serializable
sealed class Field<T> {
    abstract val columnName: String?
    abstract val value: T?

    protected fun validate() {
        check(hasSingleNotNullParam()) { "Please specify only 1 param" }
    }

    private fun hasSingleNotNullParam() = setOf(columnName, value)
        .count { it != null } == 1


    protected abstract fun asColumn(): ExpressionWithColumnType<T>
    protected abstract fun asLiteral(): ExpressionWithColumnType<T>

    open fun asExpression(): ExpressionWithColumnType<T> {
        return columnName?.let {
            asColumn()
        } ?: asLiteral()
    }

    open fun isColumn() = columnName != null

    @Serializable
    @SerialName("StringField")
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
    @SerialName("DateField")
    data class DateField(
        override val columnName: String? = null,
        override val value: @Contextual LocalDate? = null
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
    @SerialName("LongField")
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

    @Serializable
    @SerialName("AggregateField")
    data class AggregateField(
        val field: Field<Long>,
        val aggregationType: AggregationType = AggregationType.SUM
    ) :
        Field<Long>() {
        override val columnName: String? = null
        override val value: Long? = null

        override fun asColumn(): ExpressionWithColumnType<Long> {
            val columnFromFieldName = MarketingCampaignStatistic.Table.columnFromFieldName(columnName!!)
            check(columnFromFieldName.columnType is LongColumnType) { "Column[$columnName] is not long type" }
            @Suppress("UNCHECKED_CAST")
            return columnFromFieldName as ExpressionWithColumnType<Long>
        }

        override fun asLiteral(): ExpressionWithColumnType<Long> = longLiteral(value!!)

        @Suppress("UNCHECKED_CAST")
        override fun asExpression(): ExpressionWithColumnType<Long> {
            return Coalesce(
                when (aggregationType) {
                    AggregationType.SUM ->
                        field.asExpression().sum()
                    AggregationType.MIN ->
                        field.asExpression().min() as ExpressionWithColumnType<Long>
                    AggregationType.MAX ->
                        field.asExpression().max() as ExpressionWithColumnType<Long>
                    AggregationType.AVERAGE ->
                        field.asExpression().avg(0)
                            .castTo<Long>(LongColumnType())
                    AggregationType.COUNT ->
                        field.asExpression().count()
                } as ExpressionWithColumnType<Long>,
                longLiteral(0)
            )
        }

        enum class AggregationType {
            SUM, COUNT, AVERAGE, MIN, MAX
        }
    }

    @Serializable
    @SerialName("CalculatedField")
    data class CalculatedField(
        val calculationType: CalculationType = CalculationType.PLUS,
        val first: Field<Long>,
        val second: Field<Long>
    ) : Field<Long>() {
        override val columnName: String? = null
        override val value: Long? = null

        enum class CalculationType {
            PLUS, MINUS, TIMES, DIVIDE
        }

        override fun asColumn(): ExpressionWithColumnType<Long> {
            TODO("Not yet implemented")
        }

        override fun asLiteral(): ExpressionWithColumnType<Long> {
            TODO("Not yet implemented")
        }

        override fun asExpression(): ExpressionWithColumnType<Long> {
            return when (calculationType) {
                CalculationType.PLUS -> first.asExpression().plus(second.asExpression())
                CalculationType.MINUS -> first.asExpression().minus(second.asExpression())
                CalculationType.TIMES -> first.asExpression().times(second.asExpression())
                CalculationType.DIVIDE -> first.asExpression().div(second.asExpression())
            }.castTo(LongColumnType())
        }
    }
}
