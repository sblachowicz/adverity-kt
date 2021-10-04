package io.properit.adveritywarehouse.util

import io.properit.adveritywarehouse.db.Campaign
import io.properit.adveritywarehouse.db.CampaignRepository
import io.properit.adveritywarehouse.db.DailyStat
import io.properit.adveritywarehouse.db.DailyStatRepository
import io.properit.adveritywarehouse.db.Datasource
import io.properit.adveritywarehouse.db.DatasourceRepository
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class DbImportTool(
    private val restTemplate: RestTemplate,
    private val dailyStatRepository: DailyStatRepository,
    private val campaignRepository: CampaignRepository,
    private val datasourceRepository: DatasourceRepository
) {
    private val log = LoggerFactory.getLogger(DbImportTool::class.java)

    @Value("\${adverity.csv.data.url}")
    private lateinit var adverityDataUrl: String

    @Value("\${adverity.csv.data.daily.date.format}")
    private lateinit var adverityDataDailyDateFormat: String

    @EventListener(ContextRefreshedEvent::class)
    fun importCsvDataIntoDb() {
        val response = restTemplate.getForObject(adverityDataUrl, String::class.java)

        val campaigns = mutableSetOf<Campaign>()
        val datasources = mutableSetOf<Datasource>()
        val dataRows = mutableListOf<ImportRow>()

        response?.let {
            val csvFormat = CSVFormat
                .Builder
                .create()
                .setHeader("Datasource", "Campaign", "Daily", "Clicks", "Impressions")
                .setSkipHeaderRecord(true)
                .build()
            val parser = CSVParser.parse(response, csvFormat)
            parser.forEach { csvRecord ->
                val importRow = convertCsvRecordToImportRow(csvRecord)
                campaigns.add(Campaign(name = importRow.campaign))
                datasources.add(Datasource(name = importRow.datasource))
                dataRows.add(importRow)
            }
        }
        insertAll(campaigns, datasources, dataRows)
    }

    private fun insertAll(
        campaigns: MutableSet<Campaign>,
        datasources: MutableSet<Datasource>,
        dataRows: MutableList<ImportRow>
    ) {
        val campaignsByName = campaignRepository.saveAll(campaigns).associateBy { it.name }
        val datasourcesByName = datasourceRepository.saveAll(datasources).associateBy { it.name }

        dataRows.forEach {
            val dailyStat = DailyStat(
                datasource = datasourcesByName.getValue(it.datasource),
                campaign = campaignsByName.getValue(it.campaign),
                daily = it.daily,
                clicks = it.clicks,
                impressions = it.impressions
            )
            dailyStatRepository.save(dailyStat)
        }
    }

    private fun convertCsvRecordToImportRow(csvRecord: CSVRecord): ImportRow {
        val datasource = csvRecord.get("Datasource").trim()
        val campaign = csvRecord.get("Campaign").trim()
        val daily = LocalDate.parse(csvRecord.get("Daily"), DateTimeFormatter.ofPattern(adverityDataDailyDateFormat))
        val clicks = csvRecord.get("Clicks").toInt()
        val impressions = csvRecord.get("Impressions").toLong()

        return ImportRow(
            datasource = datasource,
            campaign = campaign,
            daily = daily,
            clicks = clicks,
            impressions = impressions
        )
    }
}

data class ImportRow(
    val datasource: String,
    val campaign: String,
    val daily: LocalDate,
    val clicks: Int,
    val impressions: Long,
)
