package io.properit.adveritywarehouse.service

import io.properit.adveritywarehouse.db.Datasource
import io.properit.adveritywarehouse.db.DatasourceRepository
import io.properit.adveritywarehouse.exception.ResourceNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DatasourceService(
    private val datasourceRepository: DatasourceRepository
) {
    private val log = LoggerFactory.getLogger(DatasourceService::class.java)

    fun getAll(): List<Datasource> {
        log.info("Getting all datasources")
        return datasourceRepository.findAll()
    }

    fun getDatasourceById(datasourceId: Long): Datasource {
        log.info("Getting datasource: $datasourceId")
        return datasourceRepository.findById(datasourceId).orElseThrow {
            ResourceNotFoundException("Datasource not found!")
        }
    }
}