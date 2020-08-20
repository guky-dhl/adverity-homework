package homework.domain

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import homework.infrastructure.RepositoryTest
import homework.infrastructure.TestSubject
import homework.infrastructure.dropCreate
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.sql.SchemaUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LoadStatisticShould : RepositoryTest() {

    val fileRows = csvReader()
        .readAllWithHeader("/data-file.csv".asResourceStream())
    val campaignStatistics = MarketingCampaignStatisticRepository()

    @TestSubject
    lateinit var loadStatistic: LoadStatistic

    @BeforeEach
    internal fun setUp() {
        inTestTransaction {
            SchemaUtils.dropCreate(MarketingCampaignStatistic.Table)
            loadStatistic = LoadStatistic(campaignStatistics, db)
        }
    }

    @Test
    fun `load file to database`() {
        inTestTransaction {
            fileRows.size shouldBe campaignStatistics.all().toList().size
        }
    }

    private fun String.asResourceStream() = this@LoadStatisticShould.javaClass.getResource(this).openStream()!!
}
