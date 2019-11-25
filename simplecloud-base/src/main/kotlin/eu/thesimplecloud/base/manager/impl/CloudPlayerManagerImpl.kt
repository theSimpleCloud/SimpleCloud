package eu.thesimplecloud.base.manager.impl

import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.ICommunicationPromise
import eu.thesimplecloud.lib.player.AbstractCloudPlayerManager
import eu.thesimplecloud.lib.player.ICloudPlayer
import eu.thesimplecloud.lib.player.text.CloudText
import eu.thesimplecloud.lib.service.ICloudService
import java.util.*

class CloudPlayerManagerImpl : AbstractCloudPlayerManager() {
    
    override fun getCloudPlayer(uniqueId: UUID): ICommunicationPromise<ICloudPlayer> {
    }

    override fun getCloudPlayer(name: String): ICommunicationPromise<ICloudPlayer> {
    }

    override fun sendMessageToPlayer(cloudPlayer: ICloudPlayer, cloudText: CloudText) {
    }

    override fun sendPlayerToService(cloudPlayer: ICloudPlayer, cloudService: ICloudService): ICommunicationPromise<Unit> {
    }

    override fun kickPlayer(cloudPlayer: ICloudPlayer, message: String) {
    }

    override fun sendTitle(cloudPlayer: ICloudPlayer, title: String, subTitle: String, fadeIn: Int, stay: Int, fadeOut: Int) {
    }
}