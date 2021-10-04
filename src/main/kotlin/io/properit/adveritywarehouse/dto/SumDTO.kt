package io.properit.adveritywarehouse.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class SumDTO(
    @JsonProperty("sum")
    val sum: Long,

    @JsonProperty("date_from")
    val dateFrom: LocalDate,

    @JsonProperty("date_to")
    val dateTo: LocalDate,

    @JsonProperty("aggregation_by")
    val aggregationType: String,

    @JsonProperty("sum_type")
    val sumType: Metrics
)

enum class Metrics {
    CLICKS, IMPRESSIONS
}
