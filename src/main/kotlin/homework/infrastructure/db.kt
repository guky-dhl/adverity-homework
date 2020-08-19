package homework.infrastructure

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table

fun SchemaUtils.dropCreate(vararg tables: Table) {
    drop(*tables)
    create(*tables)
}

