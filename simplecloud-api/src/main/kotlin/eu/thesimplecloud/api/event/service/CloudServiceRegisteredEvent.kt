package eu.thesimplecloud.api.event.service

import eu.thesimplecloud.api.service.ICloudService

/**
 * Called when a service updated the first time on a network component.
 */
class CloudServiceRegisteredEvent(cloudService: ICloudService) : CloudServiceEvent(cloudService)
