@file:UseContextualSerialization(BigDecimal::class)

package homework.api.dto.field

import homework.infrastructure.defaultDecimalColumn
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseContextualSerialization
import org.jetbrains.exposed.sql.*
import java.math.BigDecimal

@Serializable
@SerialName("AggregateField")
data class AggregateField(
    val field: Field<BigDecimal>,
    val aggregationType: AggregationType = AggregationType.SUM
) :
    Field<BigDecimal>() {

    @Suppress("UNCHECKED_CAST")
    override fun asExpression(): ExpressionWithColumnType<BigDecimal> {
        return Coalesce(
            when (aggregationType) {
                AggregationType.SUM ->
                    field.asExpression().sum()
                AggregationType.MIN ->
                    field.asExpression().min()
                AggregationType.MAX ->
                    field.asExpression().max()
                AggregationType.AVERAGE ->
                    field.asExpression().avg(4)
                AggregationType.COUNT ->
                    field.asExpression()
                        .count()
                        .castTo(defaultDecimalColumn)
            },
            LiteralOp(defaultDecimalColumn, BigDecimal.ZERO)
        )
    }

    enum class AggregationType {
        SUM, COUNT, AVERAGE, MIN, MAX
    }
}
