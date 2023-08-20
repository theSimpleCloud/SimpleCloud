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

package eu.thesimplecloud.plugin.impl

import eu.thesimplecloud.api.event.service.CloudServiceUnregisteredEvent
import eu.thesimplecloud.api.listenerextension.cloudListener
import eu.thesimplecloud.api.network.packets.service.PacketIOCopyService
import eu.thesimplecloud.api.network.packets.service.PacketIOStopCloudService
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.impl.AbstractCloudServiceManager
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.plugin.startup.CloudPlugin

class CloudServiceManagerImpl : AbstractCloudServiceManager() {

    override fun stopService(cloudService: ICloudService): ICommunicationPromise<Unit> {
        CloudPlugin.instance.connectionToManager.sendUnitQuery(PacketIOStopCloudService(cloudService.getName()))
        return cloudListener<CloudServiceUnregisteredEvent>()
            .addCondition { it.cloudService == cloudService }
            .unregisterAfterCall()
            .toUnitPromise()
    }

    override fun copyService(cloudService: ICloudService, path: String): ICommunicationPromise<Unit> {
        return CloudPlugin.instance.connectionToManager.sendUnitQuery(
            PacketIOCopyService(cloudService, path),
            20 * 1000
        )
    }
}