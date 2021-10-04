package io.properit.adveritywarehouse.db

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "datasources")
data class Datasource(
    @Id
    @GeneratedValue
    val id: Long = 0,

    @Column(unique = true)
    val name: String
)
