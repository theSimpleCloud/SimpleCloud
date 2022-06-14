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

package eu.thesimplecloud

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.eventapi.BasicEventManager
import eu.thesimplecloud.api.eventapi.IEventManager
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.api.language.ILanguageManager
import eu.thesimplecloud.api.message.IMessageChannelManager
import eu.thesimplecloud.api.network.component.INetworkComponent
import eu.thesimplecloud.api.player.ICloudPlayerManager
import eu.thesimplecloud.api.screen.ICommandExecuteManager
import eu.thesimplecloud.api.service.ICloudServiceManager
import eu.thesimplecloud.api.service.version.IServiceVersionHandler
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroupManager
import eu.thesimplecloud.api.sync.list.manager.ISynchronizedObjectListManager
import eu.thesimplecloud.api.sync.`object`.IGlobalPropertyHolder
import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap
import io.mockk.mockk

open class EmptyMockCloudAPIImpl(val eventManager: BasicEventManager) : CloudAPI() {
    override fun getCloudServiceGroupManager(): ICloudServiceGroupManager {
        return mockk()
    }

    override fun getCloudServiceManager(): ICloudServiceManager {
        return mockk()
    }

    override fun getCloudPlayerManager(): ICloudPlayerManager {
        return mockk()
    }

    override fun getEventManager(): IEventManager {
        return eventManager
    }

    override fun getCommandExecuteManager(): ICommandExecuteManager {
        return mockk()
    }

    override fun getGlobalPropertyHolder(): IGlobalPropertyHolder {
        return mockk()
    }

    override fun getThisSidesCommunicationBootstrap(): ICommunicationBootstrap {
        return mockk()
    }

    override fun getSynchronizedObjectListManager(): ISynchronizedObjectListManager {
        throw UnsupportedOperationException()
    }

    override fun getServiceVersionHandler(): IServiceVersionHandler {
        return mockk()
    }

    override fun getLanguageManager(): ILanguageManager {
        return mockk()
    }

    override fun getMessageChannelManager(): IMessageChannelManager {
        return mockk()
    }

    override fun getThisSidesName(): String {
        return ""
    }

    override fun getThisSidesNetworkComponent(): INetworkComponent {
        return mockk()
    }

    override fun getThisSidesCloudModule(): ICloudModule {
        throw UnsupportedOperationException()
    }
}