package eu.thesimplecloud.base.wrapper.startup

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.client.CloudClientType
import eu.thesimplecloud.client.packets.PacketOutCloudClientLogin
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.handler.IConnectionHandler
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher

class ConnectionHandlerImpl : IConnectionHandler {
    override fun onConnectionActive(connection: IConnection) {
        Launcher.instance.consoleSender.sendMessage("wrapper.connected", "Connected to the manager.")

        if (connection === Wrapper.instance.communicationClient) {
            Wrapper.instance.communicationClient.sendUnitQuery(PacketOutCloudClientLogin(CloudClientType.WRAPPER)).then {
                CloudAPI.instance.getSynchronizedObjectListManager().registerSynchronizedObjectList(CloudAPI.instance.getWrapperManager())
            }
        }
    }

    override fun onConnectionInactive(connection: IConnection) {
        if (connection === Wrapper.instance.communicationClient) {
            Wrapper.instance.resetWrapperAndStartReconnectLoop(Launcher.instance.launcherConfigLoader.loadConfig())
        }
    }

    override fun onFailure(connection: IConnection, ex: Throwable) {
        Launcher.instance.logger.exception(ex)
    }
}