package eu.thesimplecloud.plugin.startup

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.handler.IConnectionHandler

class ConnectionHandlerImpl : IConnectionHandler {
    override fun onConnectionActive(connection: IConnection) {
        println("[SimpleCloud] Connected to the Manager.")
    }

    override fun onConnectionInactive(connection: IConnection) {
    }

    override fun onFailure(connection: IConnection, ex: Throwable) {
        CloudPlugin.instance.cloudServicePlugin.shutdown()
    }
}