package io.properit.adveritywarehouse.service

import io.properit.adveritywarehouse.db.DailyStat
import io.properit.adveritywarehouse.db.DailyStatRepository
import io.properit.adveritywarehouse.dto.CtrDTO
import io.properit.adveritywarehouse.dto.ImpressionsDTO
import io.properit.adveritywarehouse.dto.Metrics
import io.properit.adveritywarehouse.dto.SumDTO
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class DailyStatsService(
    private val dailyStatRepository: DailyStatRepository,
    private val campaignService: CampaignService,
    private val datasourceService: DatasourceService
) {
    private val log = LoggerFactory.getLogger(DailyStatsService::class.java)

    fun getTotalsForDatasourceAndDateRange(
        datasourceId: Long,
        dateFrom: LocalDate,
        dateTo: LocalDate,
        metricsType: Metrics
    ): SumDTO {
        log.info("Total sum for $metricsType datasourceId: $datasourceId fromDate: $dateFrom toDate: $dateTo")
        val datasource = datasourceService.getDatasourceById(datasourceId)
        val dailyStats = dailyStatRepository.findByDatasourceAndDailyBetween(datasource, dateFrom, dateTo)
        val sum = calculateSum(dailyStats, metricsType)
        return SumDTO(
            sum, dateFrom, dateTo, datasource.name, metricsType
        )
    }

    fun getTotalsForCampaignAndDateRange(
        campaignId: Long,
        dateFrom: LocalDate,
        dateTo: LocalDate,
        metricsType: Metrics
    ): SumDTO {
        log.info("Total sum for $metricsType campaignId: $campaignId fromDate: $dateFrom toDate: $dateTo")
        val campaign = campaignService.getCampaignById(campaignId)
        val dailyStats = dailyStatRepository.findByCampaignAndDailyBetween(campaign, dateFrom, dateTo)
        val sum = calculateSum(dailyStats, metricsType)
        return SumDTO(
            sum, dateFrom, dateTo, campaign.name, metricsType
        )
    }

    private fun calculateSum(dailyStats: List<DailyStat>, aggregationType: Metrics): Long {
        return when(aggregationType) {
            Metrics.IMPRESSIONS -> dailyStats.sumOf { it.impressions }
            Metrics.CLICKS -> dailyStats.sumOf { it.clicks }.toLong()
        }

    }

    fun calculateCtrForDatasourcesAndCampaigns(
        datasourceId: Long,
        campaignId: Long
    ): CtrDTO? {
        log.info("Request for CTR calculation for datasource: $datasourceId and campaign: $campaignId")

        // Both datasource and campaign present
        val campaign = campaignService.getCampaignById(campaignId)
        val datasource = datasourceService.getDatasourceById(datasourceId)
        val stats = dailyStatRepository.findByCampaignAndDatasource(campaign, datasource)
        val impressionsSum = stats.sumOf { it.impressions }
        val clicksSum = stats.sumOf { it.clicks }.toLong()

        return if (clicksSum != 0L) {
            val ctr = (clicksSum.toDouble()/impressionsSum.toDouble())*100.0
            CtrDTO(ctr, campaign, datasource)
        } else {
            log.error("Total clicks sum is 0 cannot calculate CTR")
            CtrDTO(0.0, campaign, datasource)
        }
    }

    fun getDailyImpressions(): List<ImpressionsDTO> {
        log.info("Daily impressions")
        return dailyStatRepository.findDailyImpressionsOrdered().map {
            val date = LocalDate.parse(it[0].toString())
            val impressions = it[1].toString().toLong()
            ImpressionsDTO(date, impressions)
        }
    }
}
