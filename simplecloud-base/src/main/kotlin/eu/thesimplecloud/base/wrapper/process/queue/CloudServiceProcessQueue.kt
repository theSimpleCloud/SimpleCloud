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

package eu.thesimplecloud.base.wrapper.process.queue

import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.base.wrapper.process.CloudServiceProcess
import eu.thesimplecloud.base.wrapper.process.ICloudServiceProcess
import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.launcher.startup.Launcher
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

class CloudServiceProcessQueue {

    private val queue = LinkedBlockingQueue<ICloudServiceProcess>()
    private val startingServices = ArrayList<ICloudServiceProcess>()

    private fun getMaxSimultaneouslyStartingServices() = Wrapper.instance.getThisWrapper().getMaxSimultaneouslyStartingServices()

    fun addToQueue(cloudService: ICloudService) {
        Launcher.instance.consoleSender.sendProperty("wrapper.service.queued", cloudService.getName())
        val cloudServiceProcess = CloudServiceProcess(cloudService)
        this.queue.add(cloudServiceProcess)
        Wrapper.instance.cloudServiceProcessManager.registerServiceProcess(cloudServiceProcess)
        Wrapper.instance.updateWrapperData()
    }

    fun getStartingOrQueuedServiceAmount() = this.startingServices.size + this.queue.size

    fun startThread() {
        thread(start = true, isDaemon = true) {
            while (true) {
                val startingServicesRemoved = startingServices.removeIf { cloudServiceProcess ->
                    cloudServiceProcess.getCloudService().getState() == ServiceState.VISIBLE ||
                            cloudServiceProcess.getCloudService().getState() == ServiceState.INVISIBLE ||
                            cloudServiceProcess.getCloudService().getState() == ServiceState.CLOSED
                }
                val canStartService = queue.isNotEmpty() && startingServices.size < getMaxSimultaneouslyStartingServices()
                if (canStartService) {
                    val cloudServiceProcess = queue.poll()
                    thread { cloudServiceProcess.start() }
                    startingServices.add(cloudServiceProcess)
                }
                if (startingServicesRemoved || canStartService)
                    Wrapper.instance.updateWrapperData()
                Thread.sleep(200)
            }
        }
    }

    fun clearQueue() {
        queue.forEach { Wrapper.instance.cloudServiceProcessManager.unregisterServiceProcess(it) }
        queue.clear()
    }

}