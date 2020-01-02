package eu.thesimplecloud.api.events.service

import eu.thesimplecloud.api.service.ICloudService

class CloudServiceUpdatedEvent(cloudService: ICloudService) : CloudServiceEvent(cloudService)
