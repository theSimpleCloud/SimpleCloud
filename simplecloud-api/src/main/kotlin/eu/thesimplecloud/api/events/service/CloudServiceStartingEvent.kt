package eu.thesimplecloud.api.events.service

import eu.thesimplecloud.api.service.ICloudService

class CloudServiceStartingEvent(cloudService: ICloudService) : CloudServiceEvent(cloudService)
