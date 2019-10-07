package eu.thesimplecloud.lib.events.service

import eu.thesimplecloud.lib.service.ICloudService

class CloudServiceRegisteredEvent(cloudService: ICloudService) : CloudServiceEvent(cloudService)
