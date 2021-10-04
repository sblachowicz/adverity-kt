package io.properit.adveritywarehouse.service

import io.properit.adveritywarehouse.db.Campaign
import io.properit.adveritywarehouse.db.CampaignRepository
import io.properit.adveritywarehouse.exception.ResourceNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CampaignService(private val campaignRepository: CampaignRepository) {

    private val log = LoggerFactory.getLogger(CampaignService::class.java)

    fun getAll(): List<Campaign> {
        log.info("Generating list of all campaigns")
        return campaignRepository.findAll()
    }

    fun getCampaignById(campaignId: Long): Campaign {
        return campaignRepository.findById(campaignId).orElseThrow {
            log.error("Campaign not found with id $campaignId")
            ResourceNotFoundException("Campaign not found!")
        }
    }
}
