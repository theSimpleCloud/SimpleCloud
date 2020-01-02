package eu.thesimplecloud.api.events.service

import eu.thesimplecloud.api.eventapi.IEvent
import eu.thesimplecloud.api.service.ICloudService

open class CloudServiceEvent(val cloudService: ICloudService) : IEvent