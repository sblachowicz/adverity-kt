package io.properit.adveritywarehouse.dto

import io.properit.adveritywarehouse.db.Campaign
import io.properit.adveritywarehouse.db.Datasource

data class CtrDTO(
    val ctr: Double,
    val campaign: Campaign,
    val datasource: Datasource
)
