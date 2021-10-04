package io.properit.adveritywarehouse.db

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface DailyStatRepository : JpaRepository<DailyStat, Long> {
    fun findByDatasourceAndDailyBetween(datasource: Datasource, fromDate: LocalDate, toDate: LocalDate): List<DailyStat>
    fun findByCampaignAndDailyBetween(campaign: Campaign, fromDate: LocalDate, toDate: LocalDate): List<DailyStat>
    fun findByCampaignAndDatasource(campaign: Campaign, datasource: Datasource): List<DailyStat>

    @Query("SELECT ds.daily as date, SUM(ds.impressions) as impressionsSum FROM DailyStat AS ds GROUP BY ds.daily ORDER BY ds.daily ASC")
    fun findDailyImpressionsOrdered(): List<Array<Any>>
}

@Repository
interface CampaignRepository : JpaRepository<Campaign, Long>

@Repository
interface DatasourceRepository : JpaRepository<Datasource, Long>
