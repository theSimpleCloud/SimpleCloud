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

package eu.thesimplecloud.base.manager.impl

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.service.CloudServiceConnectedEvent
import eu.thesimplecloud.api.event.service.CloudServiceUnregisteredEvent
import eu.thesimplecloud.api.exception.UnreachableComponentException
import eu.thesimplecloud.api.listenerextension.cloudListener
import eu.thesimplecloud.api.network.packets.service.PacketIOCopyService
import eu.thesimplecloud.api.network.packets.service.PacketIOStopCloudService
import eu.thesimplecloud.api.network.packets.service.PacketIOWrapperStartService
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.api.service.impl.AbstractCloudServiceManager
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.launcher.startup.Launcher
import java.io.File
import java.text.SimpleDateFormat
import java.util.stream.Collectors

class CloudServiceManagerImpl : AbstractCloudServiceManager() {

    override fun stopService(cloudService: ICloudService): ICommunicationPromise<Unit> {
        Launcher.instance.screenManager.getScreen(cloudService.getName()).let {
            if (it != null) {
                it.getAllSavedMessages().parallelStream().collect(Collectors.joining("\n"))?.let { screenMessage ->
                    val groupName = cloudService.getServiceGroup().getName()
                    val file = File("logs/$groupName/")
                    if (!file.exists()) file.mkdirs()
                    val date = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(System.currentTimeMillis())
                    File("${file.absolutePath}/${cloudService.getName()}-$date.txt").writeText(screenMessage)
                }
            }
        }

        when (cloudService.getState()) {
            ServiceState.CLOSED -> {
                return CommunicationPromise.failed(IllegalStateException("Service is already closed"))
            }
            ServiceState.PREPARED -> {
                Manager.instance.serviceHandler.removeServiceFromQueue(cloudService)
                cloudService.setState(ServiceState.CLOSED)
                CloudAPI.instance.getCloudServiceManager().delete(cloudService)
                return CommunicationPromise.UNIT_PROMISE
            }
            ServiceState.STARTING, ServiceState.VISIBLE, ServiceState.INVISIBLE -> {
                val wrapper = cloudService.getWrapper()
                val wrapperClient = Manager.instance.communicationServer.getClientManager()
                    .getClientByClientValue(wrapper)
                wrapperClient?.sendUnitQuery(PacketIOStopCloudService(cloudService.getName()))
            }
        }

        return cloudListener<CloudServiceUnregisteredEvent>()
            .addCondition { it.cloudService == cloudService }
            .unregisterAfterCall()
            .toUnitPromise()
    }

    override fun startService(cloudService: ICloudService): ICommunicationPromise<Unit> {
        if (cloudService.isActive() || cloudService.getState() == ServiceState.CLOSED) throw IllegalStateException("Can not start started service.")
        val wrapper = cloudService.getWrapper()
        val wrapperClient = Manager.instance.communicationServer.getClientManager().getClientByClientValue(wrapper)
        check(wrapperClient != null) { "Can not find client of wrapper to start service ${cloudService.getName()} on." }
        wrapperClient.sendUnitQuery(PacketIOWrapperStartService(cloudService.getName()))
        return cloudListener<CloudServiceConnectedEvent>()
            .addCondition { it.cloudService == cloudService }
            .unregisterAfterCall()
            .toUnitPromise()
    }

    override fun copyService(cloudService: ICloudService, path: String): ICommunicationPromise<Unit> {
        if (!cloudService.isActive())
            return CommunicationPromise.failed(IllegalStateException("Cannot copy inactive service"))
        val wrapper = cloudService.getWrapper()
        val wrapperClient = Manager.instance.communicationServer.getClientManager()
            .getClientByClientValue(wrapper)
        wrapperClient ?: return CommunicationPromise.failed(UnreachableComponentException("Wrapper is not reachable"))
        return wrapperClient.sendUnitQuery(PacketIOCopyService(cloudService, path), 20 * 1000)
    }
}