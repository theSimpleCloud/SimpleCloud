package eu.thesimplecloud.api.events.service

import eu.thesimplecloud.api.service.ICloudService

class CloudServiceConnectedEvent(cloudService: ICloudService) : CloudServiceEvent(cloudService)
