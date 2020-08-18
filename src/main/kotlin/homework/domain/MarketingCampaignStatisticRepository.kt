package homework.domain

import cool.db.DataRepository

class MarketingCampaignStatisticRepository :
    DataRepository<MarketingCampaignStatistic.Table, Long, MarketingCampaignStatistic>(
        MarketingCampaignStatistic.Table,
        MarketingCampaignStatistic::class
    )
