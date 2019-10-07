package eu.thesimplecloud.lib.events.service

import eu.thesimplecloud.lib.service.ICloudService

class CloudServiceUpdatedEvent(cloudService: ICloudService) : CloudServiceEvent(cloudService)
