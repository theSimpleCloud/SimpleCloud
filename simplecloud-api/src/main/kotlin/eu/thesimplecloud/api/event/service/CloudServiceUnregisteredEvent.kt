package eu.thesimplecloud.api.event.service

import eu.thesimplecloud.api.service.ICloudService

class CloudServiceUnregisteredEvent(cloudService: ICloudService) : CloudServiceEvent(cloudService)
