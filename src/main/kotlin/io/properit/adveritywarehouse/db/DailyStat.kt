package io.properit.adveritywarehouse.db

import java.time.LocalDate
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "daily_stats", indexes = [
    Index(name = "datasource_idx", columnList = "datasource_id"),
    Index(name = "campaign_idx", columnList = "campaign_id")
])
data class DailyStat(
    @Id
    @GeneratedValue
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "datasource_id", nullable = false)
    val datasource: Datasource,

    @ManyToOne
    @JoinColumn(name = "campaign_id", nullable = false)
    val campaign: Campaign,

    @Column(nullable = false)
    val daily: LocalDate,

    @Column(nullable = false)
    val clicks: Int,

    @Column(nullable = false)
    val impressions: Long,
)
