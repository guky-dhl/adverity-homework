package homework.api.dto.field

import homework.domain.MarketingCampaignStatistic
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

@Nested
class DateFieldShould {

    @Test
    fun `be created with date column`() {
        DateField(MarketingCampaignStatistic::at.name)
    }

    @Test
    fun `not be created with both parameters`() {
        val e = shouldThrow<IllegalStateException> {
            DateField(MarketingCampaignStatistic::at.name, LocalDate.now())
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
