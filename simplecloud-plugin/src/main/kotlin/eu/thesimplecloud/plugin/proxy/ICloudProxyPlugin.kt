package eu.thesimplecloud.plugin.proxy

import eu.thesimplecloud.lib.player.connection.IPlayerConnection
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.plugin.ICloudServicePlugin

interface ICloudProxyPlugin : ICloudServicePlugin {

    fun addServiceToProxy(cloudService: ICloudService)

    /**
     * Sends a login request to the manager and returns the response synchronously
     */
    fun sendLoginRequestAndAwait(playerConnection: IPlayerConnection) {

    }

}