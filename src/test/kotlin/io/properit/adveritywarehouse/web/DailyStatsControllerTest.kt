package io.properit.adveritywarehouse.web

import io.properit.adveritywarehouse.dto.Metrics
import io.properit.adveritywarehouse.service.CampaignService
import io.properit.adveritywarehouse.service.DailyStatsService
import io.properit.adveritywarehouse.service.DatasourceService
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.LocalDate
import java.time.ZoneOffset

@WebMvcTest(
    controllers = [
        DailyStatsController::class
    ]
)
class DailyStatsControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var dailyStatsService: DailyStatsService

    @MockBean
    private lateinit var campaignService: CampaignService

    @MockBean
    private lateinit var datasourceService: DatasourceService

    @Test
    fun `get datasources`() {
        mvc.get("/stats/datasources").andExpect {
            status { isOk() }
        }

        Mockito.verify(datasourceService).getAll()
    }

    @Test
    fun `get campaigns`() {
        mvc.get("/stats/campaigns").andExpect {
            status { isOk() }
        }

        Mockito.verify(campaignService).getAll()
    }

    @Test
    fun `get datasource totals`() {
        // given
        val datasourceId = 1L
        val dateFrom = LocalDate.now(ZoneOffset.UTC)
        val dateTo = LocalDate.now(ZoneOffset.UTC)

        // when
        mvc.get("/stats/datasource/{datasourceId}/totals", datasourceId) {
            param("dateFrom", dateFrom.toString())
            param("dateTo", dateTo.toString())
            param("metricsType", Metrics.CLICKS.name)
        }.andExpect {
            status { isOk() }
        }

        // then
        Mockito.verify(dailyStatsService, Mockito.times(1)).getTotalsForDatasourceAndDateRange(
            datasourceId,
            dateFrom,
            dateTo,
            Metrics.CLICKS
        )
    }
}