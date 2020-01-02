package eu.thesimplecloud.api.events.service

import eu.thesimplecloud.api.service.ICloudService

class CloudServiceRegisteredEvent(cloudService: ICloudService) : CloudServiceEvent(cloudService)
