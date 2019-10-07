package eu.thesimplecloud.lib.events.service

import eu.thesimplecloud.lib.eventapi.IEvent
import eu.thesimplecloud.lib.service.ICloudService

open class CloudServiceEvent(val cloudService: ICloudService) : IEvent