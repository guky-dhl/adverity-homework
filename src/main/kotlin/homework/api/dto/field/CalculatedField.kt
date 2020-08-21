@file:UseContextualSerialization(BigDecimal::class)

package homework.api.dto.field

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseContextualSerialization
import org.jetbrains.exposed.sql.ExpressionWithColumnType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.div
import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.SqlExpressionBuilder.times
import java.math.BigDecimal

@Serializable
@SerialName("CalculatedField")
data class CalculatedField(
    val calculationType: CalculationType = CalculationType.PLUS,
    val first: Field<BigDecimal>,
    val second: Field<BigDecimal>
) : Field<BigDecimal>() {

    enum class CalculationType {
        PLUS, MINUS, TIMES, DIVIDE
    }

    override fun asExpression(): ExpressionWithColumnType<BigDecimal> {
        return when (calculationType) {
            CalculationType.PLUS -> first.asExpression().plus(second.asExpression())
            CalculationType.MINUS -> first.asExpression().minus(second.asExpression())
            CalculationType.TIMES -> first.asExpression().times(second.asExpression())
            CalculationType.DIVIDE -> first.asExpression().div(second.asExpression())
        }
    }
}
