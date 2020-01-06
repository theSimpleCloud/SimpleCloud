package eu.thesimplecloud.api.event.service

import eu.thesimplecloud.api.service.ICloudService

/**
 * Called when a service was updated.
 */
class CloudServiceUpdatedEvent(cloudService: ICloudService) : CloudServiceEvent(cloudService)
