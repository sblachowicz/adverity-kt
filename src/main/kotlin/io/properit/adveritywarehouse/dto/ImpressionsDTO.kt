package io.properit.adveritywarehouse.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

class ImpressionsDTO(
    @JsonProperty("date")
    val date: LocalDate,

    @JsonProperty("impressions_sum")
    val impressionsSum: Long
)
