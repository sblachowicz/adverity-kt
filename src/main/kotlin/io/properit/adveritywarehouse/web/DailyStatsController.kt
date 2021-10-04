package io.properit.adveritywarehouse.web

import io.properit.adveritywarehouse.dto.Metrics
import io.properit.adveritywarehouse.service.CampaignService
import io.properit.adveritywarehouse.service.DailyStatsService
import io.properit.adveritywarehouse.service.DatasourceService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import javax.validation.constraints.NotNull

@RestController
@RequestMapping("/stats")
class DailyStatsController(
    private val dailyStatsService: DailyStatsService,
    private val campaignService: CampaignService,
    private val datasourceService: DatasourceService
) {

    @GetMapping("/datasources")
    fun getAllDataSources() = datasourceService.getAll()

    @GetMapping("/campaigns")
    fun getAllCampaigns() = campaignService.getAll()

    @GetMapping("/datasource/{datasourceId}/totals")
    fun getSumForDatasource(
        @NotNull @PathVariable(name = "datasourceId") datasourceId: Long,
        @NotNull @RequestParam(name = "dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dateFrom: LocalDate,
        @NotNull @RequestParam(name = "dateTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dateTo: LocalDate,
        @NotNull @RequestParam(name = "metricsType") metricsType: Metrics
    ) = dailyStatsService.getTotalsForDatasourceAndDateRange(datasourceId, dateFrom, dateTo, metricsType)

    @GetMapping("/campaign/{campaignId}/totals")
    fun getSumForCampaign(
        @NotNull @PathVariable(name = "campaignId") campaignId: Long,
        @NotNull @RequestParam(name = "dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dateFrom: LocalDate,
        @NotNull @RequestParam(name = "dateTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dateTo: LocalDate,
        @NotNull @RequestParam(name = "metricsType") metricsType: Metrics
    ) = dailyStatsService.getTotalsForCampaignAndDateRange(campaignId, dateFrom, dateTo, metricsType)

    @GetMapping("/ctr")
    fun ctrForDataSourceAndCampaign(
        @NotNull @RequestParam(name = "datasourceId") datasourceId: Long,
        @NotNull @RequestParam(name = "campaignId") campaignId: Long,
    ) = dailyStatsService.calculateCtrForDatasourcesAndCampaigns(datasourceId, campaignId)

    @GetMapping("/impressions")
    fun dailyImpressions() = dailyStatsService.getDailyImpressions()
}
