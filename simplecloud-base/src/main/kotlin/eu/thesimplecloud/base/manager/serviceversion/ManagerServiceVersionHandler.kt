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

package eu.thesimplecloud.base.manager.serviceversion

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.extension.sendPacketToAllAuthenticatedClients
import eu.thesimplecloud.api.network.packets.serviceversion.PacketIOServiceVersions
import eu.thesimplecloud.api.service.version.ServiceVersionHandler
import eu.thesimplecloud.api.service.version.loader.CombinedServiceVersionLoader
import eu.thesimplecloud.api.service.version.loader.LocalServiceVersionHandler
import eu.thesimplecloud.base.manager.startup.Manager

/**
 * Created by IntelliJ IDEA.
 * Date: 29/01/2021
 * Time: 22:11
 * @author Frederick Baier
 */
class ManagerServiceVersionHandler : ServiceVersionHandler(CombinedServiceVersionLoader.loadVersions()) {

    fun reloadServiceVersions() {
        val allVersions = CombinedServiceVersionLoader.loadVersions()
        this.versions = allVersions

        val packet = PacketIOServiceVersions(allVersions)
        Manager.instance.communicationServer.getClientManager().sendPacketToAllAuthenticatedClients(packet)
    }

    fun deleteServiceVersion(name: String) {
        if (!doesVersionExist(name))
            throw NoSuchElementException("Service-Version does not exist")
        if (isVersionInUse(name))
            throw IllegalStateException("Service-Version is still in use")

        val list = this.versions.toMutableList()
        list.removeIf { it.name == name }
        LocalServiceVersionHandler().deleteServiceVersion(name)
        reloadServiceVersions()
    }

    fun isVersionInUse(name: String): Boolean {
        return CloudAPI.instance.getCloudServiceGroupManager().getAllCachedObjects().any { it.getServiceVersion().name == name }
    }

    fun doesVersionExist(name: String): Boolean {
        return this.versions.any { it.name == name }
    }

}