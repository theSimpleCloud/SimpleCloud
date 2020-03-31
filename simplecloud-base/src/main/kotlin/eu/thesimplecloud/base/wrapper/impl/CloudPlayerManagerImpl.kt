package eu.thesimplecloud.base.wrapper.impl

import eu.thesimplecloud.api.location.ServiceLocation
import eu.thesimplecloud.api.location.SimpleLocation
import eu.thesimplecloud.api.player.AbstractCloudPlayerManager
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.player.IOfflineCloudPlayer
import eu.thesimplecloud.api.player.text.CloudText
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

class CloudPlayerManagerImpl : AbstractCloudPlayerManager() {

    override fun getCloudPlayer(uniqueId: UUID): ICommunicationPromise<ICloudPlayer> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun getCloudPlayer(name: String): ICommunicationPromise<ICloudPlayer> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun sendMessageToPlayer(cloudPlayer: ICloudPlayer, cloudText: CloudText): ICommunicationPromise<Unit> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun connectPlayer(cloudPlayer: ICloudPlayer, cloudService: ICloudService): ICommunicationPromise<Unit> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun kickPlayer(cloudPlayer: ICloudPlayer, message: String) {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun sendTitle(cloudPlayer: ICloudPlayer, title: String, subTitle: String, fadeIn: Int, stay: Int, fadeOut: Int) {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun forcePlayerCommandExecution(cloudPlayer: ICloudPlayer, command: String) {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun sendActionbar(cloudPlayer: ICloudPlayer, actionbar: String) {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun setUpdates(cloudPlayer: ICloudPlayer, update: Boolean, serviceName: String) {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun teleportPlayer(cloudPlayer: ICloudPlayer, location: SimpleLocation): ICommunicationPromise<Unit> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun teleportPlayer(cloudPlayer: ICloudPlayer, location: ServiceLocation): ICommunicationPromise<Unit> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun hasPermission(cloudPlayer: ICloudPlayer, permission: String): ICommunicationPromise<Boolean> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun getLocationOfPlayer(cloudPlayer: ICloudPlayer): ICommunicationPromise<ServiceLocation> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun sendPlayerToLobby(cloudPlayer: ICloudPlayer): ICommunicationPromise<Unit> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun getOfflineCloudPlayer(name: String): ICommunicationPromise<IOfflineCloudPlayer> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun getOfflineCloudPlayer(uniqueId: UUID): ICommunicationPromise<IOfflineCloudPlayer> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }
}