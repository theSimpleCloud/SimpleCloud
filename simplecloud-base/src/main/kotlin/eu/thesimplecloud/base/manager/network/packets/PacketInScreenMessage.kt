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
import eu.thesimplecloud.api.screen.ICommandExecutable
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.launcher.startup.Launcher

class PacketInScreenMessage : JsonPacket() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val cloudClientType = this.jsonLib.getObject("cloudClientType", NetworkComponentType::class.java)
            ?: return contentException("cloudClientType")
        val name = this.jsonLib.getString("name") ?: return contentException("name")
        val message = this.jsonLib.getString("message") ?: return contentException("message")

        val commandExecutable: ICommandExecutable? = when (cloudClientType) {
            NetworkComponentType.SERVICE -> CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(name)
            NetworkComponentType.WRAPPER -> CloudAPI.instance.getWrapperManager().getWrapperByName(name)
            else -> throw IllegalArgumentException("NetworkComponentType")
        }
        commandExecutable ?: return failure(NoSuchElementException("Cannot find service / wrapper by name: $name"))

        val screenManager = Launcher.instance.screenManager
        screenManager.addScreenMessage(commandExecutable, "[$name]$message")
        return unit()
    }
}