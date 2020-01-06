package eu.thesimplecloud.api.event.service

import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceState

/**
 * Called whe a service chances its state from [ServiceState.PREPARED] to [ServiceState.STARTING]
 */
class CloudServiceStartingEvent(cloudService: ICloudService) : CloudServiceEvent(cloudService)
