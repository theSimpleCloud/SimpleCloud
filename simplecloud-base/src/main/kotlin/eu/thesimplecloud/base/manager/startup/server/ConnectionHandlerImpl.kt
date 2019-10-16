package eu.thesimplecloud.base.manager.startup.server

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.handler.IConnectionHandler
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.lib.CloudLib
import java.net.InetSocketAddress

class ConnectionHandlerImpl : IConnectionHandler {

    override fun onConnectionActive(connection: IConnection) {
        val host = connection.getHost()!!
        val wrapperByHost = CloudLib.instance.getWrapperManager().getWrapperByHost(host)
        if (wrapperByHost == null) {
            Launcher.instance.consoleSender.sendMessage("manager.connection.unknown-host", "A client connected from an unknown host: %HOST%", host)
            connection.closeConnection()
            return
        }
    }

    override fun onConnectionInactive(connection: IConnection) {
    }

    override fun onFailure(connection: IConnection, ex: Throwable) {
        Launcher.instance.logger.exception(ex)
    }
}