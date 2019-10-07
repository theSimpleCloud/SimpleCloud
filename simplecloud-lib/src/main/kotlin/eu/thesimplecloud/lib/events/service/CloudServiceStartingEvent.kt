package eu.thesimplecloud.lib.events.service

import eu.thesimplecloud.lib.service.ICloudService

class CloudServiceStartingEvent(cloudService: ICloudService) : CloudServiceEvent(cloudService)
