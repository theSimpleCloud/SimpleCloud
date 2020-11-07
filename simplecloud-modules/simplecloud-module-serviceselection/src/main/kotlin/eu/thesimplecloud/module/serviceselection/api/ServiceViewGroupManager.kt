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

package eu.thesimplecloud.module.serviceselection.api

import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup

/**
 * Created by IntelliJ IDEA.
 * Date: 01.07.2020
 * Time: 11:47
 * @author Frederick Baier
 */
class ServiceViewGroupManager(
        val group: ICloudServiceGroup
) {

    private val serviceViewers: MutableList<AbstractServiceViewer> = ArrayList()

    fun addServiceViewers(vararg serviceViewer: AbstractServiceViewer) {
        this.serviceViewers.addAll(serviceViewer)
    }

    fun removeServiceViewer(serviceViewer: AbstractServiceViewer) {
        this.serviceViewers.remove(serviceViewer)
    }

    fun sortWaitingServicesToViewers() {
        val allServicesWaiting = getAllServicesWaitingForViewer()
        val allVisibleServices = allServicesWaiting.filter { it.getState() == ServiceState.VISIBLE }
        val allStartingServices = allServicesWaiting.filter { it.getState() == ServiceState.STARTING }
        allVisibleServices.forEach { searchViewerForService(it) }
        allStartingServices.forEach { searchViewerForService(it) }
    }

    fun updateAllViewers() {
        this.serviceViewers.forEach { it.updateView() }
    }

    private fun searchViewerForService(service: ICloudService) {
        require(!hasViewer(service)) { "Service does already has a viewer" }
        val newViewer = getNewViewerForService(service)
        newViewer?.service = service
        //don't update here. The update method will be called for all viewers after sorting the services to the viewers.
    }

    private fun getNewViewerForService(service: ICloudService): AbstractServiceViewer? {
        require(!hasViewer(service)) { "Service does already has a viewer" }
        return if (service.getState() == ServiceState.VISIBLE) {
            getVacantOrStartingViewer()
        } else {
            //state must be starting
            getVacantViewer()
        }
    }

    private fun getVacantOrStartingViewer(): AbstractServiceViewer? {
        return this.serviceViewers.firstOrNull { it.isVacant() || it.isCurrentServiceStarting() }
    }

    private fun getVacantViewer(): AbstractServiceViewer? {
        return this.serviceViewers.firstOrNull { it.isVacant() }
    }

    private fun hasViewer(service: ICloudService): Boolean {
        return this.serviceViewers.any { it.service == service }
    }

    private fun getAllServicesWaitingForViewer(): List<ICloudService> {
        return getAllServicesToDisplay().filter { !hasViewer(it) }
    }

    private fun getAllServicesToDisplay(): List<ICloudService> {
        return this.group.getAllServices().filter { it.isStartingOrVisible() }
    }

}