package eu.thesimplecloud.api.events.service

import eu.thesimplecloud.api.service.ICloudService

class CloudServiceUnregisteredEvent(cloudService: ICloudService) : CloudServiceEvent(cloudService)
