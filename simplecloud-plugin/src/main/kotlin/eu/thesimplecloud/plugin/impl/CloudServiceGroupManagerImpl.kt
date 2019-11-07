package eu.thesimplecloud.plugin.impl

import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.responsehandler.ObjectPacketResponseHandler
import eu.thesimplecloud.lib.network.packets.service.PacketIOStartCloudService
import eu.thesimplecloud.lib.network.packets.servicegroup.PacketIOCreateServiceGroup
import eu.thesimplecloud.lib.network.packets.servicegroup.PacketIODeleteServiceGroup
import eu.thesimplecloud.lib.network.reponsehandler.CloudServiceGroupResponseHandler
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.impl.DefaultCloudService
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.lib.servicegroup.impl.AbstractCloudServiceGroupManager
import eu.thesimplecloud.plugin.startup.CloudPlugin
import java.lang.IllegalStateException

class CloudServiceGroupManagerImpl : AbstractCloudServiceGroupManager() {
    override fun createServiceGroup(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<ICloudServiceGroup> {
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOCreateServiceGroup(cloudServiceGroup), CloudServiceGroupResponseHandler())
    }

    override fun startNewService(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<ICloudService> {
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOStartCloudService(cloudServiceGroup.getName()), ObjectPacketResponseHandler(DefaultCloudService::class.java)) as ICommunicationPromise<ICloudService>
    }

    override fun deleteServiceGroup(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<Unit> {
        val unitPromise = CloudPlugin.instance.communicationClient.newPromise<Unit>()
        val packetPromise = CloudPlugin.instance.communicationClient.sendQuery(PacketIODeleteServiceGroup(cloudServiceGroup.getName()), ObjectPacketResponseHandler(Boolean::class.java))
        packetPromise.addResultListener {
            if (it == null || it == false){
                unitPromise.setFailure(IllegalStateException("Can not delete CloudServiceGroup while services of this group are registered."))
            } else {
                unitPromise.setSuccess(Unit)
            }
        }
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIODeleteServiceGroup(cloudServiceGroup.getName()))
    }

}