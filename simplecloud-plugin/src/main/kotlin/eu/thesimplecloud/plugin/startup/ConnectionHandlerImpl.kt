package eu.thesimplecloud.plugin.startup

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.handler.IConnectionHandler

class ConnectionHandlerImpl : IConnectionHandler {
    override fun onConnectionActive(connection: IConnection) {
    }

    override fun onConnectionInactive(connection: IConnection) {
    }

    override fun onFailure(connection: IConnection, ex: Throwable) {
        println("[SimpleCloud] an error occurred:")
        ex.printStackTrace()
    }
}