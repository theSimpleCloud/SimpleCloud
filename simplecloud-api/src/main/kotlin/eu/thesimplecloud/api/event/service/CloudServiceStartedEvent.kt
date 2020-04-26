package eu.thesimplecloud.api.event.service

import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceState

/**
 * Called when the state of a [cloudService] changed to [ServiceState.VISIBLE] or [ServiceState.INVISIBLE] and was not [ServiceState.VISIBLE] or [ServiceState.INVISIBLE] before.
 */
class CloudServiceStartedEvent(cloudService: ICloudService) : CloudServiceEvent(cloudService)
