package eu.thesimplecloud.lib.events.service

import eu.thesimplecloud.lib.service.ICloudService

class CloudServiceUnregisteredEvent(cloudService: ICloudService) : CloudServiceEvent(cloudService)
