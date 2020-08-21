package homework.api.dto.field

import homework.api.dto.field.StringField
import homework.domain.MarketingCampaignStatistic
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
class StringFieldShould {

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
