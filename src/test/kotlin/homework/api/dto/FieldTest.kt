package homework.api.dto

import homework.api.dto.Field.SimpleField.*
import homework.domain.MarketingCampaignStatistic
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate.now

internal class FieldTest {

    @Nested
    inner class LongFieldShould {

        @Test
        fun `be created with long column`() {
            LongField(MarketingCampaignStatistic::clicks.name)
        }

        @Test
        fun `not be created with both parameters`() {
            val e = shouldThrow<IllegalStateException> {
                LongField(MarketingCampaignStatistic::clicks.name, 1)
            }
            e.message shouldContain "only 1 param"
        }

        @Test
        fun `not be converted to expression when column target is not long type`() {
            val e = shouldThrow<IllegalStateException> {
                LongField(MarketingCampaignStatistic::dataSource.name).asExpression()
            }
            e.message shouldContain MarketingCampaignStatistic::dataSource.name
            e.message shouldContain "is not"
            e.message shouldContain "LongColumnType"
        }
    }

    @Nested
    inner class DateFieldShould {

        @Test
        fun `be created with date column`() {
            DateField(MarketingCampaignStatistic::at.name)
        }

        @Test
        fun `not be created with both parameters`() {
            val e = shouldThrow<IllegalStateException> {
                DateField(MarketingCampaignStatistic::at.name, now())
            }
            e.message shouldContain "only 1 param"
        }

        @Test
        fun `not be converted to expression when column target is not date type`() {
            val e = shouldThrow<IllegalStateException> {
                DateField(MarketingCampaignStatistic::dataSource.name).asExpression()
            }
            e.message shouldContain MarketingCampaignStatistic::dataSource.name
            e.message shouldContain "is not"
        }

    }

    @Nested
    inner class StringFieldShould {

        @Test
        fun `be created with string column`() {
            StringField(MarketingCampaignStatistic::dataSource.name)
        }

        @Test
        fun `not be created with both parameters`() {
            val e = shouldThrow<IllegalStateException> {
                StringField(MarketingCampaignStatistic::dataSource.name, "")
            }
            e.message shouldContain "only 1 param"
        }

        @Test
        fun `not be converted to expression when column target is not string type`() {
            val e = shouldThrow<IllegalStateException> {
                StringField(MarketingCampaignStatistic::at.name).asExpression()
            }
            e.message shouldContain MarketingCampaignStatistic::at.name
            e.message shouldContain "is not"
            e.message shouldContain "StringColumnType"
        }
    }
}
