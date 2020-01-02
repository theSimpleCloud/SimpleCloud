package eu.thesimplecloud.api.events.service

import eu.thesimplecloud.api.service.ICloudService

class CloudServiceJoinableEvent(cloudService: ICloudService) : CloudServiceEvent(cloudService)
