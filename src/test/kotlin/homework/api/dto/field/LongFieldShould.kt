package homework.api.dto.field

import homework.api.dto.field.LongField
import homework.domain.MarketingCampaignStatistic
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
class LongFieldShould {

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
