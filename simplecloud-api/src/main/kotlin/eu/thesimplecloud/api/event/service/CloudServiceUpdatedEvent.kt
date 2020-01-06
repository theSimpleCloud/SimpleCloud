package eu.thesimplecloud.api.event.service

import eu.thesimplecloud.api.service.ICloudService

class CloudServiceUpdatedEvent(cloudService: ICloudService) : CloudServiceEvent(cloudService)
