package io.properit.adveritywarehouse.service

import io.properit.adveritywarehouse.db.Campaign
import io.properit.adveritywarehouse.db.DailyStat
import io.properit.adveritywarehouse.db.DailyStatRepository
import io.properit.adveritywarehouse.db.Datasource
import io.properit.adveritywarehouse.dto.Metrics
import io.properit.adveritywarehouse.exception.ResourceNotFoundException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.time.LocalDate
import java.time.ZoneOffset

@SpringBootTest(classes = [DailyStatsService::class])
class DailyStatsServiceTest {

    @MockBean
    private lateinit var dailyStatRepository: DailyStatRepository

    @MockBean
    private lateinit var campaignService: CampaignService

    @MockBean
    private lateinit var datasourceService: DatasourceService

    @Autowired
    private lateinit var dailyStatsService: DailyStatsService

    @Test
    fun `get totals for datasource - datasource not found - should throw resource not found`() {
        // given
        val datasourceId = 1L
        val dateFrom = LocalDate.now(ZoneOffset.UTC)
        val dateTo = LocalDate.now(ZoneOffset.UTC)
        val metricsType = Metrics.CLICKS

        Mockito.`when`(datasourceService.getDatasourceById(datasourceId)).thenThrow(ResourceNotFoundException("Not found"))

        // when
        assertThrows<ResourceNotFoundException> {
            dailyStatsService.getTotalsForDatasourceAndDateRange(datasourceId, dateFrom, dateTo, metricsType)
        }

        // then
        Mockito.verify(datasourceService).getDatasourceById(datasourceId)
        Mockito.verifyNoInteractions(dailyStatRepository)
    }

    @Test
    fun `get totals for datasource - datasource found - should return dto object`() {
        // given
        val datasourceId = 1L
        val dateFrom = LocalDate.now(ZoneOffset.UTC)
        val dateTo = LocalDate.now(ZoneOffset.UTC)
        val metricsType = Metrics.CLICKS

        val datasource = Datasource(datasourceId, "ds")
        Mockito.`when`(datasourceService.getDatasourceById(datasourceId)).thenReturn(datasource)

        val ds1 = DailyStat(1L, datasource, Campaign(1L, "camp1"), LocalDate.now(), 2, 2)
        val ds2 = DailyStat(1L, datasource, Campaign(2L, "camp2"), LocalDate.now(), 3, 6)
        val dailyStats = listOf<DailyStat>(ds1, ds2)
        Mockito.`when`(dailyStatRepository.findByDatasourceAndDailyBetween(datasource, dateFrom, dateTo)).thenReturn(dailyStats)

        // when
        val result = dailyStatsService.getTotalsForDatasourceAndDateRange(datasourceId, dateFrom, dateTo, metricsType)

        // then
        Assertions.assertNotNull(result)
        Assertions.assertEquals(result.aggregationType, datasource.name)
        Assertions.assertEquals(result.sum, 5)
        Mockito.verify(datasourceService).getDatasourceById(datasourceId)
        Mockito.verify(dailyStatRepository).findByDatasourceAndDailyBetween(datasource, dateFrom, dateTo)
    }
}