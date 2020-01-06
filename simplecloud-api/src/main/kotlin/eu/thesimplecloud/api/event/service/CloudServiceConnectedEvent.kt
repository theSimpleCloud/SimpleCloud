package eu.thesimplecloud.api.event.service

import eu.thesimplecloud.api.service.ICloudService

/**
 * Called when the [cloudService] connects to the manager
 */
class CloudServiceConnectedEvent(cloudService: ICloudService) : CloudServiceEvent(cloudService)
