package homework.domain

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LoadStatistic(
    private val campaignStatistics: MarketingCampaignStatisticRepository,
    createdForCorrectDIGrahp: Database
) {
    private val pattern = DateTimeFormatter.ofPattern("MM/dd/yy")!!

    init {
        transaction {
            csvReader()
                .readAllWithHeader("/data-file.csv".asResourceStream())
                .map(::Row)
                .map { row ->
                    MarketingCampaignStatistic(
                        row["Datasource"],
                        row["Campaign"],
                        row["Daily"],
                        row["Clicks"],
                        row["Impressions"]
                    )
                }.forEach { campaignStatistics.save(it) }
        }
    }

    private fun String.asResourceStream() = this@LoadStatistic.javaClass.getResource(this).openStream()!!

    internal inner class Row(private val map: Map<String, String>) {
        inline operator fun <reified T> get(key: String): T {
            val value = map[key] ?: error("$key required")
            return when (T::class) {
                String::class -> value as T
                Long::class -> map[key]?.toLong() as T
                LocalDate::class -> LocalDate.parse(value, pattern) as T
                else -> throw IllegalArgumentException()
            }
        }
    }
}
