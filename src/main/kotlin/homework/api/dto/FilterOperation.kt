package homework.api.dto

import org.jetbrains.exposed.sql.Between
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.ExpressionWithColumnType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.notInList

enum class FilterOperation(val value: String) {

    LT("<"), LE("<="), EQ("="), NE("!="), GE(">="), GT(">"), BETWEEN("between"), IN("in"), NOT_IN("not in");


    operator fun <T : Comparable<T>> invoke(
        one: ExpressionWithColumnType<T>,
        other: Expression<T>,
        max: Expression<T>? = null
    ): Op<Boolean> {
        return when (this) {
            LT -> one less other
            LE -> one lessEq other
            EQ -> one eq other
            NE -> one neq other
            GE -> one greaterEq other
            GT -> one greater other
            BETWEEN -> {
                Between(one, other, checkNotNull(max) { "Between requires not null max param" })
            }
            else -> throw java.lang.IllegalArgumentException("Unsupported filter operation")
        }
    }

    operator fun <T> invoke(one: ExpressionWithColumnType<T>, other: Iterable<T>): Op<Boolean> {
        return when (this) {
            IN -> one.inList(other)
            NOT_IN -> one.notInList(other)
            else -> throw java.lang.IllegalArgumentException("Unsupported filter operation")
        }
    }

    override fun toString(): String {
        return value
    }
}
