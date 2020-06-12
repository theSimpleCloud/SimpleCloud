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

package eu.thesimplecloud.base.manager.network.packets

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.client.NetworkComponentType
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher

class PacketInCloudClientLogin() : JsonPacket() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val host = connection.getHost()!!
        val cloudClientType = this.jsonLib.getObject("cloudClientType", NetworkComponentType::class.java)
                ?: return contentException("cloudClientType")
        connection as IConnectedClient<IConnectedClientValue>
        CloudAPI.instance.getWrapperManager().sendAllCachedObjectsToConnection(connection).awaitCoroutine()
        CloudAPI.instance.getCloudServiceManager().sendAllCachedObjectsToConnection(connection).awaitCoroutine()
        CloudAPI.instance.getTemplateManager().sendAllCachedObjectsToConnection(connection).awaitCoroutine()
        CloudAPI.instance.getCloudServiceGroupManager().sendAllCachedObjectsToConnection(connection).awaitCoroutine()
        when (cloudClientType) {
            NetworkComponentType.SERVICE -> {
                val name = this.jsonLib.getString("name") ?: return contentException("name")
                val cloudService = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(name)
                        ?: return failure(NoSuchElementException("Service not found"))
                connection.setClientValue(cloudService)
                cloudService.setAuthenticated(true)
                CloudAPI.instance.getCloudServiceManager().update(cloudService)
                CloudAPI.instance.getCloudServiceManager().sendUpdateToConnection(cloudService, connection).awaitCoroutine()
                Launcher.instance.consoleSender.sendMessage("manager.login.service", "Service %SERVICE%", cloudService.getName(), " logged in.")
            }
            NetworkComponentType.WRAPPER -> {
                val wrapperInfo = CloudAPI.instance.getWrapperManager().getWrapperByHost(host)
                        ?: return failure(NoSuchElementException("Wrapper not found"))
                connection.setClientValue(wrapperInfo)
                wrapperInfo.setAuthenticated(true)
                CloudAPI.instance.getWrapperManager().update(wrapperInfo)
                CloudAPI.instance.getWrapperManager().sendUpdateToConnection(wrapperInfo, connection).awaitCoroutine()
                connection.sendUnitQuery(PacketOutSetWrapperName(wrapperInfo.getName())).awaitCoroutine()
                Launcher.instance.consoleSender.sendMessage("manager.login.wrapper", "Wrapper %WRAPPER%", wrapperInfo.getName(), " logged in.")
            }
        }

        return unit()
    }
}