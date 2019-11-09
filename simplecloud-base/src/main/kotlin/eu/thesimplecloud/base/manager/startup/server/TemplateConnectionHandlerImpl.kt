package eu.thesimplecloud.base.manager.startup.server

import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.handler.IConnectionHandler
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import eu.thesimplecloud.lib.screen.ICommandExecutable
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.utils.IAuthenticatable
import eu.thesimplecloud.lib.wrapper.IWrapperInfo
import eu.thesimplecloud.lib.wrapper.IWritableWrapperInfo
import java.io.File
import java.lang.IllegalStateException
import java.net.InetSocketAddress

class TemplateConnectionHandlerImpl : IConnectionHandler {

    override fun onConnectionInactive(connection: IConnection) {
    }

    override fun onConnectionActive(connection: IConnection) {
        val host = connection.getHost()!!
        val wrapperByHost = CloudLib.instance.getWrapperManager().getWrapperByHost(host)
        if (wrapperByHost == null) {
            Launcher.instance.consoleSender.sendMessage("manager.connection.unknown-host", "A client connected from an unknown host: %HOST%", host)
            connection.closeConnection()
            return
        }
        val templatesSyncPromise = connection.getCommunicationBootstrap().getDirectorySyncManager().getDirectorySync(File(DirectoryPaths.paths.templatesPath))?.syncDirectory(connection)
        val modulesSyncPromise = connection.getCommunicationBootstrap().getDirectorySyncManager().getDirectorySync(File(DirectoryPaths.paths.modulesPath))?.syncDirectory(connection)
        if (templatesSyncPromise == null || modulesSyncPromise == null) {
            throw IllegalStateException("Failed to send modules and templates to a connected wrapper.")
        }
        templatesSyncPromise.syncUninterruptibly()
        modulesSyncPromise.syncUninterruptibly()
        wrapperByHost as IWritableWrapperInfo
        wrapperByHost.setTemplatesReceived(true)
        CloudLib.instance.getWrapperManager().updateWrapper(wrapperByHost)
    }

    override fun onFailure(connection: IConnection, ex: Throwable) {
        Launcher.instance.logger.exception(ex)
    }
}