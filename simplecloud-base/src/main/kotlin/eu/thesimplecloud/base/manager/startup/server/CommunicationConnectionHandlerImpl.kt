package eu.thesimplecloud.base.manager.startup.server

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.handler.IConnectionHandler
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.screen.ICommandExecutable
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.utils.IAuthenticatable
import eu.thesimplecloud.api.wrapper.IWritableWrapperInfo

class CommunicationConnectionHandlerImpl : IConnectionHandler {

    override fun onConnectionActive(connection: IConnection) {
        val host = connection.getHost()!!
        val wrapperByHost = CloudAPI.instance.getWrapperManager().getWrapperByHost(host)
        if (wrapperByHost == null) {
            Launcher.instance.consoleSender.sendMessage("manager.connection.unknown-host", "A client connected from an unknown host: %HOST%", host)
            connection.closeConnection()
            return
        }
    }

    override fun onConnectionInactive(connection: IConnection) {
        connection as IConnectedClient<ICommandExecutable>
        val clientValue = connection.getClientValue()
        clientValue ?: return
        clientValue as IAuthenticatable
        clientValue.setAuthenticated(false)

        if (clientValue is IWritableWrapperInfo) {
            clientValue.setTemplatesReceived(false)
            clientValue.setUsedMemory(0)
            CloudAPI.instance.getWrapperManager().updateWrapper(clientValue)
        }

        if (clientValue is ICloudService)
            CloudAPI.instance.getCloudServiceManager().updateCloudService(clientValue)
    }

    override fun onFailure(connection: IConnection, ex: Throwable) {
        Launcher.instance.logger.exception(ex)
    }
}