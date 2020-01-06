package eu.thesimplecloud.api.event.service

import eu.thesimplecloud.api.service.ICloudService

class CloudServiceRegisteredEvent(cloudService: ICloudService) : CloudServiceEvent(cloudService)
