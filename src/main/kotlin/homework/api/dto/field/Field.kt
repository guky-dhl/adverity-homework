package homework.api.dto.field

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ExpressionWithColumnType
import java.math.BigDecimal
import java.time.LocalDate

@Serializable
abstract class Field<T> {
    open fun isColumn(): Boolean = when (this) {
        is SimpleField -> this.isColumn()
        else -> false
    }

    abstract fun asExpression(): ExpressionWithColumnType<T>

    companion object {
        fun wrapValue(value: Any): SimpleField<*> {
            return when (value) {
                is Long -> LongField(value = value)
                is String -> StringField(value = value)
                is LocalDate -> DateField(value = value)
                is BigDecimal -> DecimalField(value = value)
                else -> throw IllegalStateException("Unsupported response value[$value] of type ${value::class}")
            }
        }
    }
}



