package eu.thesimplecloud.base.manager.network.packets.template

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.api.wrapper.IWritableWrapperInfo
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.filetransfer.directory.IDirectorySync
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher
import java.io.File

class PacketInGetTemplates() : JsonPacket() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val wrapperByHost = CloudAPI.instance.getWrapperManager().getWrapperByHost(connection.getHost()!!)
                ?: throw IllegalStateException("No Wrapper object found for Wrapper by host " + connection.getHost())
        Launcher.instance.consoleSender.sendMessage("manager.templates.synchronization", "Synchronizing templates with Wrapper %WRAPPER%", wrapperByHost.getName(), "...")
        val templatesDirectorySync = connection.getCommunicationBootstrap().getDirectorySyncManager().getDirectorySync(File(DirectoryPaths.paths.templatesPath))
        val modulesDirectorySync = connection.getCommunicationBootstrap().getDirectorySyncManager().getDirectorySync(File(DirectoryPaths.paths.modulesPath))
        syncDirectory(templatesDirectorySync, connection)
        syncDirectory(modulesDirectorySync, connection)
        Launcher.instance.consoleSender.sendMessage("manager.templates.synchronization.complete", "Synchronized templates with Wrapper %WRAPPER%", wrapperByHost.getName(), ".")
        wrapperByHost as IWritableWrapperInfo
        wrapperByHost.setTemplatesReceived(true)
        CloudAPI.instance.getWrapperManager().update(wrapperByHost)
        return unit()
    }

    private fun syncDirectory(directorySync: IDirectorySync?, connection: IConnection) {
        checkNotNull(directorySync) { "Failed to send modules and templates to a connected wrapper." }
        val promise = directorySync.syncDirectory(connection)
        promise.awaitUninterruptibly()
    }
}