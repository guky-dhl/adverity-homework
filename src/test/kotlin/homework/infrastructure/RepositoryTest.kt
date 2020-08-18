package homework.infrastructure

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction

abstract class RepositoryTest {
    private val db: Database by lazy { Database.connect("jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;", "org.h2.Driver") }

    protected fun <T> inTestTransaction(statement: Transaction.() -> T): T {
        return transaction(db, statement)
    }
}
