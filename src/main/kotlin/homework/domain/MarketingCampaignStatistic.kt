package homework.domain

import cool.db.entity.DataEntityIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.`java-time`.date
import java.time.LocalDate

class MarketingCampaignStatistic(
    val dataSource: String,
    val campaignName: String,
    val at: LocalDate,
    val clicks: Long,
    val impressions: Long
) {

    object Table :
        DataEntityIdTable<Long, MarketingCampaignStatistic>("marketing_campaigns", MarketingCampaignStatistic::class) {
        override val id: Column<Long> = long("id").autoIncrement("seq")
        override val primaryKey: PrimaryKey = PrimaryKey(id)

        val dataSource = varchar("data_source", 200).bindProperty(MarketingCampaignStatistic::dataSource)
        val campaignName = varchar("campaign_name", 200).bindProperty(MarketingCampaignStatistic::campaignName)
        val at = date("at").bindProperty(MarketingCampaignStatistic::at)
        val clicks = long("clicks").bindProperty(MarketingCampaignStatistic::clicks)
        val impressions = long("impressions").bindProperty(MarketingCampaignStatistic::impressions)
    }
}
