/*
 * MIT License
 *
 * Copyright (C) 2020-2022 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.base.manager.network.packets.template

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.filetransfer.directory.IDirectorySync
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.launcher.startup.Launcher
import java.io.File

class PacketInGetTemplates() : JsonPacket() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val wrapperByHost = CloudAPI.instance.getWrapperManager().getWrapperByHost(connection.getHost()!!)
            ?: throw IllegalStateException("No Wrapper object found for Wrapper by host " + connection.getHost())
        Launcher.instance.consoleSender.sendProperty("manager.templates.synchronization", wrapperByHost.getName())
        val templatesDirectorySync = connection.getCommunicationBootstrap().getDirectorySyncManager()
            .getDirectorySync(File(DirectoryPaths.paths.templatesPath))
        val modulesDirectorySync = connection.getCommunicationBootstrap().getDirectorySyncManager()
            .getDirectorySync(File(DirectoryPaths.paths.modulesPath))
        syncDirectory(templatesDirectorySync, connection)
        syncDirectory(modulesDirectorySync, connection)
        Launcher.instance.consoleSender.sendProperty(
            "manager.templates.synchronization.complete",
            wrapperByHost.getName()
        )
        return unit()
    }

    private fun syncDirectory(directorySync: IDirectorySync?, connection: IConnection) {
        checkNotNull(directorySync) { "Failed to send modules and templates to a connected wrapper." }
        val promise = directorySync.syncDirectory(connection)
        promise.awaitUninterruptibly()
    }
}