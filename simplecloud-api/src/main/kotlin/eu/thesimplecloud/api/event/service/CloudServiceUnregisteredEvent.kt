package eu.thesimplecloud.api.event.service

import eu.thesimplecloud.api.service.ICloudService

/**
 * Called when a service was unregistered (stopped)
 */
class CloudServiceUnregisteredEvent(cloudService: ICloudService) : CloudServiceEvent(cloudService)
