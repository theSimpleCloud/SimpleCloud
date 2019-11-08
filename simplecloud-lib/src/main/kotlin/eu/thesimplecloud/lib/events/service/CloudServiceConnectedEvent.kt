package eu.thesimplecloud.lib.events.service

import eu.thesimplecloud.lib.service.ICloudService

class CloudServiceConnectedEvent(cloudService: ICloudService) : CloudServiceEvent(cloudService)
