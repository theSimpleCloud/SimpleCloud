/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
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

package eu.thesimplecloud.base.manager.startup.server

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.network.component.IAuthenticatable
import eu.thesimplecloud.api.screen.ICommandExecutable
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.api.sync.`object`.GlobalPropertyHolder
import eu.thesimplecloud.api.wrapper.IMutableWrapperInfo
import eu.thesimplecloud.base.manager.impl.CloudPlayerManagerImpl
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher

class CommunicationConnectionHandlerImpl : AbstractCloudConnectionHandler() {

    override fun onConnectionInactive(connection: IConnection) {
        super.onConnectionInactive(connection)
        connection as IConnectedClient<ICommandExecutable>
        val clientValue = connection.getClientValue()
        clientValue ?: return
        clientValue as IAuthenticatable
        clientValue.setAuthenticated(false)

        if (clientValue is IMutableWrapperInfo) {
            unregisterServicesRunningOnWrapper(clientValue)
            clientValue.setTemplatesReceived(false)
            clientValue.setCurrentlyStartingServices(0)
            clientValue.setUsedMemory(0)
            CloudAPI.instance.getWrapperManager().update(clientValue)
            Launcher.instance.consoleSender.sendMessage("manager.disconnect.wrapper", "Wrapper %WRAPPER%", clientValue.getName(), " disconnected.")
        }

        if (clientValue is ICloudService) {
            val playerManager = CloudAPI.instance.getCloudPlayerManager() as CloudPlayerManagerImpl
            playerManager.resetPlayerUpdates(clientValue.getName())
            CloudAPI.instance.getCloudServiceManager().update(clientValue)
            Launcher.instance.consoleSender.sendMessage("manager.disconnect.service", "Service %SERVICE%", clientValue.getName(), " disconnected.")
        }
        val globalPropertyHolder = CloudAPI.instance.getGlobalPropertyHolder() as GlobalPropertyHolder
        globalPropertyHolder.removeConnectionFromUpdates(connection)
    }

    private fun unregisterServicesRunningOnWrapper(clientValue: IMutableWrapperInfo) {
        val services = CloudAPI.instance.getCloudServiceManager().getServicesRunningOnWrapper(clientValue.getName())
        services.forEach {
            it.setState(ServiceState.CLOSED)
            it.setOnlineCount(0)
            it.setAuthenticated(false)
            it.update()
        }
        services.forEach { CloudAPI.instance.getCloudServiceManager().delete(it) }
    }

    override fun onFailure(connection: IConnection, ex: Throwable) {
        Launcher.instance.logger.exception(ex)
    }
}