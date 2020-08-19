package homework.domain

import homework.api.dto.FilterOperation.*
import homework.infrastructure.RepositoryTest
import homework.infrastructure.TestSubject
import homework.infrastructure.dropCreate
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.sql.SchemaUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate.now

internal class MarketingReportShould : RepositoryTest() {
    val dataSource = "MyDataSource"
    val campaing = "FirstCampaign"
    val marketingStatistics = MarketingCampaignStatisticRepository()
    val statsRecordCount = 100L

    @TestSubject
    val report = MarketingReport(marketingStatistics)

    @BeforeEach
    internal fun setUp() {
        inTestTransaction {
            SchemaUtils.dropCreate(MarketingCampaignStatistic.Table)
            for (i in 1..statsRecordCount) {
                val stats =
                    MarketingCampaignStatistic(dataSource, campaing, now().plusDays(i), i, i * 100)
                marketingStatistics.save(stats)
            }
        }
    }

    @Test
    fun `filter by data source`() {
        inTestTransaction {
            val by = report.by(
                marketingDataRequest {
                    +stringFilter {
                        column(MarketingCampaignStatistic::dataSource)
                        value(dataSource)
                    }
                }
            )
            by.size shouldBe statsRecordCount
        }
    }

    @Test
    fun `filter by data source and date`() {
        val days = 10L
        inTestTransaction {
            val by = report.by(
                marketingDataRequest {
                    +stringFilter {
                        column(MarketingCampaignStatistic::dataSource)
                        value(dataSource)
                    }
                    +dateFilter {
                        column(MarketingCampaignStatistic::at)
                        operation = BETWEEN
                        value(now())
                        value(now().plusDays(days))
                    }

                }
            )
            by.size shouldBe days
        }
    }

    @Test
    fun `filter by data source with in set of values`() {
        inTestTransaction {
            val by = report.by(
                marketingDataRequest {
                    +stringFilter {
                        column(MarketingCampaignStatistic::dataSource)
                        operation = IN
                        values(dataSource, "another data source")
                    }
                }
            )
            by.size shouldBe statsRecordCount
        }
    }

    @Test
    fun `filter by data source not in set of values`() {
        inTestTransaction {
            val by = report.by(
                marketingDataRequest {
                    +stringFilter {
                        column(MarketingCampaignStatistic::dataSource)
                        operation = NOT_IN
                        values(dataSource, "another data source")
                    }
                }
            )
            by.size shouldBe 0
        }
    }
}
