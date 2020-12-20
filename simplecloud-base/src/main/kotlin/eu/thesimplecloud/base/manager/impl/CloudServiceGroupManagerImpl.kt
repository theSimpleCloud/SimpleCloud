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

package eu.thesimplecloud.base.manager.impl

import eu.thesimplecloud.api.service.start.configuration.IServiceStartConfiguration
import eu.thesimplecloud.api.service.start.future.IServiceStartPromise
import eu.thesimplecloud.api.service.start.future.ServiceStartPromise
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.api.servicegroup.impl.AbstractCloudServiceGroupManager
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class CloudServiceGroupManagerImpl : AbstractCloudServiceGroupManager() {


    override fun createServiceGroup(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<ICloudServiceGroup> {
        val promise = CommunicationPromise<ICloudServiceGroup>()
        if (getServiceGroupByName(cloudServiceGroup.getName()) == null) {
            update(cloudServiceGroup)
            promise.trySuccess(cloudServiceGroup)
        } else {
            promise.setFailure(IllegalArgumentException("The name of the specified group is already in use"))
        }
        return promise
    }

    override fun update(value: ICloudServiceGroup, fromPacket: Boolean, isCalledFromDelete: Boolean): ICommunicationPromise<Unit> {
        Manager.instance.cloudServiceGroupFileHandler.save(value)
        return super.update(value, fromPacket, isCalledFromDelete)
    }

    override fun startNewService(serviceStartConfiguration: IServiceStartConfiguration): IServiceStartPromise {
        val service = try {
            Manager.instance.serviceHandler.startService(serviceStartConfiguration)
        } catch (ex: IllegalArgumentException) {
            //catch IllegalArgumentException. It will be thrown when the service to start is already registered.
            return ServiceStartPromise(CommunicationPromise.failed(ex))
        }
        return ServiceStartPromise(CommunicationPromise.of(service))
    }

    override fun delete(value: ICloudServiceGroup, fromPacket: Boolean): ICommunicationPromise<Unit> {
        Manager.instance.cloudServiceGroupFileHandler.delete(value)
        return super.delete(value, fromPacket)
    }

}