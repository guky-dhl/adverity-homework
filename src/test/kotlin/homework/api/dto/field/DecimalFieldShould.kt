package homework.api.dto.field

import homework.domain.MarketingCampaignStatistic
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal.ZERO

@Nested
class DecimalFieldShould {

    @Test
    fun `be created with long column`() {
        DecimalField(MarketingCampaignStatistic::clicks.name)
    }

    @Test
    fun `not be created with both parameters`() {
        val e = shouldThrow<IllegalStateException> {
            DecimalField(MarketingCampaignStatistic::dataSource.name, ZERO)
        }
        e.message shouldContain "only 1 param"
    }

    @Test
    fun `not be converted to expression when column target is not Decimal type`() {
        val e = shouldThrow<IllegalStateException> {
            DecimalField(MarketingCampaignStatistic::at.name).asExpression()
        }
        e.message shouldContain MarketingCampaignStatistic::at.name
        e.message shouldContain "is not"
        e.message shouldContain "DecimalColumnType"
    }
}
