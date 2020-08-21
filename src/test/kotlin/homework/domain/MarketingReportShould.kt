package homework.domain

import homework.api.dto.field.DateField
import homework.api.dto.field.LongField
import homework.api.dto.FilterOperation.*
import homework.infrastructure.RepositoryTest
import homework.infrastructure.TestSubject
import homework.infrastructure.asDecimal
import homework.infrastructure.dropCreate
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.sql.SchemaUtils
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.LocalDate.now

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class MarketingReportShould : RepositoryTest() {
    val dataSource = "MyDataSource"
    val campaing = "FirstCampaign"
    val marketingStatistics = MarketingCampaignStatisticRepository()
    val statsRecordCount = 100L
    val impressionsMultiplicand = 100L

    @TestSubject
    val report = MarketingReport()

    init {
        inTestTransaction {
            SchemaUtils.dropCreate(MarketingCampaignStatistic.Table)
            for (i in 1..statsRecordCount) {
                val stats =
                    MarketingCampaignStatistic(dataSource, campaing, now().plusDays(i), i, i * impressionsMultiplicand)
                marketingStatistics.save(stats)
            }
        }
    }

    @Nested
    inner class GroupBy {
        @Test
        fun `by columns and aggregate`() {
            inTestTransaction {
                val by = report.by(
                    marketingDataRequest {
                        +selectString(MarketingCampaignStatistic::dataSource)
                        +sum(MarketingCampaignStatistic::impressions)
                        groupBy(
                            selectString(MarketingCampaignStatistic::dataSource)
                        )
                    }
                )
                by.result.size shouldBe 1
                by.result.first().toList()[0] shouldBe selectString("MyDataSource")
                by.result.first()
                    .toList()[1] shouldBe selectDecimal((1..statsRecordCount).sumBy { it.toInt() * impressionsMultiplicand.toInt() })
            }
        }
    }

    @Nested
    inner class Dimensions {
        @Test
        fun `only requested fields`() {
            val selectedFieldCount = 3
            inTestTransaction {
                val by = report.by(
                    marketingDataRequest {
                        +selectString(MarketingCampaignStatistic::dataSource)
                        +selectDate(MarketingCampaignStatistic::at)
                        +selectLong(MarketingCampaignStatistic::clicks)
                    }
                )
                by.result.size shouldBe statsRecordCount
                by.result.first().size shouldBe selectedFieldCount
                by.result.first().toList()[0] shouldBe selectString(dataSource)
                by.result.first().toList()[1]::class shouldBe DateField::class
                by.result.first().toList()[2]::class shouldBe LongField::class
            }
        }

        @Test
        fun `aggregated sum dimension`() {
            inTestTransaction {
                val by = report.by(
                    marketingDataRequest {
                        +sum(MarketingCampaignStatistic::clicks)
                    }
                )
                by.result.size shouldBe 1
                by.result.first().size shouldBe 1
                by.result.first().toList()[0] shouldBe selectDecimal((1..100).sum())
            }
        }

        @Test
        fun `average calculation by sum and count`() {
            inTestTransaction {
                val by = report.by(
                    marketingDataRequest {
                        +(sum(MarketingCampaignStatistic::clicks) / count(MarketingCampaignStatistic::clicks))
                    }
                )
                by.result.size shouldBe 1
                by.result.first().size shouldBe 1
                by.result.first().toList()[0] shouldBe selectDecimal((1..statsRecordCount).average().asDecimal())
            }
        }
    }

    @Nested
    inner class Filter {
        @Test
        fun `by data source`() {
            inTestTransaction {
                val by = report.by(
                    marketingDataRequest {
                        +stringFilter {
                            column(MarketingCampaignStatistic::dataSource)
                            value(dataSource)
                        }
                    }
                )
                by.result.size shouldBe statsRecordCount
            }
        }

        @Test
        fun `by data source and date`() {
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
                by.result.size shouldBe days
            }
        }

        @Test
        fun `by data source with in set of values`() {
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
                by.result.size shouldBe statsRecordCount
            }
        }

        @Test
        fun `by data source not in set of values`() {
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
                by.result.size shouldBe 0
            }
        }
    }
}

