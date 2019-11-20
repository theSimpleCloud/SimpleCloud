package eu.thesimplecloud.base.manager.network.packets.template

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.filetransfer.directory.DirectorySync
import eu.thesimplecloud.clientserverapi.lib.filetransfer.directory.IDirectorySync
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.server.packets.PacketInGetPacketId
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import eu.thesimplecloud.lib.wrapper.IWritableWrapperInfo
import java.io.File
import java.lang.IllegalStateException

class PacketInGetTemplates() : JsonPacket() {

    override suspend fun handle(connection: IConnection): IPacket? {
        val wrapperByHost = CloudLib.instance.getWrapperManager().getWrapperByHost(connection.getHost()!!)
                ?: throw IllegalStateException("No Wrapper object found for Wrapper by host " + connection.getHost())
        Launcher.instance.consoleSender.sendMessage("manager.templates.synchronization", "Synchronizing templates with Wrapper %WRAPPER%", wrapperByHost.getName(), "...")
        val templatesDirectorySync = connection.getCommunicationBootstrap().getDirectorySyncManager().getDirectorySync(File(DirectoryPaths.paths.templatesPath))
        val modulesDirectorySync = connection.getCommunicationBootstrap().getDirectorySyncManager().getDirectorySync(File(DirectoryPaths.paths.modulesPath))
        syncDirectory(templatesDirectorySync, connection)
        syncDirectory(modulesDirectorySync, connection)
        Launcher.instance.consoleSender.sendMessage("manager.templates.synchronization.complete", "Synchronized templates with Wrapper %WRAPPER%", wrapperByHost.getName(), ".")
        wrapperByHost as IWritableWrapperInfo
        wrapperByHost.setTemplatesReceived(true)
        CloudLib.instance.getWrapperManager().updateWrapper(wrapperByHost)
        return null
    }

    fun syncDirectory(directorySync: IDirectorySync?, connection: IConnection) {
        checkNotNull(directorySync) { "Failed to send modules and templates to a connected wrapper." }
        val promise = directorySync.syncDirectory(connection)
        promise.syncUninterruptibly()
    }
}