package eu.thesimplecloud.base.wrapper.impl

import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.ICommunicationPromise
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroupManager

class CloudServiceGroupManagerImpl : ICloudServiceGroupManager {

    override fun updateGroup(cloudServiceGroup: ICloudServiceGroup) {
    }

    override fun createServiceGroup(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<ICloudServiceGroup> {
    }

    override fun getAllGroups(): List<ICloudServiceGroup> {
    }

    override fun clearCache() {
    }

    override fun startNewService(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<ICloudService> {
    }

    override fun deleteServiceGroup(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<Unit> {
    }
}