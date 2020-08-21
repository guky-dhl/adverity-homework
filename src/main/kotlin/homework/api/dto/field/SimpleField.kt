@file:UseContextualSerialization(BigDecimal::class, LocalDate::class)
package homework.api.dto.field

import homework.domain.MarketingCampaignStatistic
import homework.infrastructure.defaultDecimalColumn
import kotlinx.serialization.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.`java-time`.dateLiteral
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

@Serializable
sealed class SimpleField<T> : Field<T>() {
    abstract val columnName: String?
    abstract val value: T?
    abstract val columnTypeClass: KClass<*>

    protected fun validate() {
        check(hasSingleNotNullParam()) { "Please specify only 1 param" }
    }

    private fun hasSingleNotNullParam() = setOf(columnName, value)
        .count { it != null } == 1

    protected open fun asColumn(): ExpressionWithColumnType<T> {
        val columnFromFieldName = MarketingCampaignStatistic.Table.columnFromFieldName(columnName!!)
        check(columnFromFieldName.columnType::class.isSubclassOf(columnTypeClass)) { "Column[$columnName] is not $columnTypeClass" }
        @Suppress("UNCHECKED_CAST")
        return columnFromFieldName as ExpressionWithColumnType<T>
    }

    protected abstract fun asLiteral(): ExpressionWithColumnType<T>

    override fun isColumn() = columnName != null

    override fun asExpression(): ExpressionWithColumnType<T> {
        return columnName?.let {
            asColumn()
        } ?: asLiteral()
    }
}

@Serializable
@SerialName("StringField")
data class StringField(
    override val columnName: String? = null,
    override val value: String? = null
) :
    SimpleField<String>() {
    @Transient
    override val columnTypeClass = StringColumnType::class

    init {
        validate()
    }

    override fun asLiteral(): ExpressionWithColumnType<String> = stringLiteral(value!!)

}

@Serializable
@SerialName("DateField")
data class DateField(
    override val columnName: String? = null,
    override val value: LocalDate? = null
) :
    SimpleField<LocalDate>() {
    @Transient
    override val columnTypeClass = IDateColumnType::class

    init {
        validate()
    }

    override fun asLiteral(): ExpressionWithColumnType<LocalDate> = dateLiteral(value!!)
}

@Serializable
@SerialName("LongField")
data class LongField(
    override val columnName: String? = null,
    override val value: Long? = null
) :
    SimpleField<Long>() {
    @Transient
    override val columnTypeClass = LongColumnType::class

    init {
        validate()
    }

    override fun asLiteral(): ExpressionWithColumnType<Long> = longLiteral(value!!.toLong())
    fun asDecimal(): Field<BigDecimal> {
        return DecimalField(columnName, value?.toBigDecimal())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DecimalField && other !is LongField) return false

        if (other is LongField) {
            if (columnName != other.columnName) return false
            if (value != other.value) return false
        }

        if (other is DecimalField) {
            if (columnName != other.columnName) return false
            if (value?.toBigDecimal() != other.value) return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = columnName?.hashCode() ?: 0
        result = 31 * result + (value?.hashCode() ?: 0)
        return result
    }
}

@Serializable
@SerialName("DecimalField")
data class DecimalField(
    override val columnName: String? = null,
    override val value: BigDecimal? = null
) :
    SimpleField<BigDecimal>() {
    @Transient
    override val columnTypeClass: KClass<*> = DecimalColumnType::class

    init {
        validate()
    }

    override fun asColumn(): ExpressionWithColumnType<BigDecimal> {
        val columnFromFieldName = MarketingCampaignStatistic.Table.columnFromFieldName(columnName!!)
        check(
            columnFromFieldName.columnType::class.isSubclassOf(columnTypeClass)
                    || columnFromFieldName.columnType is LongColumnType
        ) { "Column[$columnName] is not $columnTypeClass" }
        @Suppress("UNCHECKED_CAST")
        return columnFromFieldName as ExpressionWithColumnType<BigDecimal>
    }

    override fun asLiteral(): ExpressionWithColumnType<BigDecimal> =
        LiteralOp(defaultDecimalColumn, value!!)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DecimalField && other !is LongField) return false

        if (other is LongField) {
            if (columnName != other.columnName) return false
            if (value != other.value?.toBigDecimal()) return false
        }

        if (other is DecimalField) {
            if (columnName != other.columnName) return false
            if (value != other.value) return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = columnName?.hashCode() ?: 0
        result = 31 * result + (value?.hashCode() ?: 0)
        return result
    }
}
