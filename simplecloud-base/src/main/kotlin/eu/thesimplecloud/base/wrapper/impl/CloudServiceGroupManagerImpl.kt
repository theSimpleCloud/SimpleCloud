package eu.thesimplecloud.base.wrapper.impl

import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.responsehandler.ObjectPacketResponseHandler
import eu.thesimplecloud.lib.network.packets.servicegroup.PacketIOCreateServiceGroup
import eu.thesimplecloud.lib.network.packets.servicegroup.PacketIODeleteServiceGroup
import eu.thesimplecloud.lib.network.packets.servicegroup.PacketIOUpdateCloudServiceGroup
import eu.thesimplecloud.lib.network.reponsehandler.CloudServiceGroupResponseHandler
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.lib.servicegroup.impl.AbstractCloudServiceGroupManager
import java.lang.IllegalStateException

class CloudServiceGroupManagerImpl : AbstractCloudServiceGroupManager() {


    override fun createServiceGroup(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<ICloudServiceGroup> {
        return Wrapper.instance.communicationClient.sendQuery(PacketIOCreateServiceGroup(cloudServiceGroup), CloudServiceGroupResponseHandler())
    }

    override fun startNewService(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<ICloudService> = TODO()

    override fun deleteServiceGroup(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<Unit> {
        val unitPromise = Wrapper.instance.communicationClient.newPromise<Unit>()
        val packetPromise = Wrapper.instance.communicationClient.sendQuery(PacketIODeleteServiceGroup(cloudServiceGroup.getName()), ObjectPacketResponseHandler(Boolean::class.java))
        packetPromise.addResultListener {
            if (it == null || it == false){
                unitPromise.setFailure(IllegalStateException("Can not delete CloudServiceGroup while services of this group are registered."))
            } else {
                unitPromise.setSuccess(Unit)
            }
        }
        return unitPromise
    }


}