package eu.thesimplecloud.api.event.service

import eu.thesimplecloud.api.service.ICloudService

class CloudServiceStartingEvent(cloudService: ICloudService) : CloudServiceEvent(cloudService)
