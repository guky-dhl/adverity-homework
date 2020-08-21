package homework.infrastructure

import org.jetbrains.exposed.sql.DecimalColumnType
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import java.math.RoundingMode

fun SchemaUtils.dropCreate(vararg tables: Table) {
    drop(*tables)
    create(*tables)
}

const val scale = 4
val defaultDecimalColumn = DecimalColumnType(Integer.MAX_VALUE, scale)
fun Double.asDecimal() = this.toBigDecimal().setScale(scale, RoundingMode.HALF_EVEN)
fun Int.asDecimal() = this.toBigDecimal().setScale(scale, RoundingMode.HALF_EVEN)
