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

package eu.thesimplecloud.api.eventapi

import eu.thesimplecloud.api.CloudAPI
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
import org.mockito.Mockito.mock

open class EmptyMockCloudAPIImpl : CloudAPI() {
    override fun getCloudServiceGroupManager(): ICloudServiceGroupManager {
        return mock(ICloudServiceGroupManager::class.java)
    }

    override fun getCloudServiceManager(): ICloudServiceManager {
        return mock(ICloudServiceManager::class.java)
    }

    override fun getCloudPlayerManager(): ICloudPlayerManager {
        return mock(ICloudPlayerManager::class.java)
    }

    override fun getEventManager(): IEventManager {
        return mock(IEventManager::class.java)
    }

    override fun getCommandExecuteManager(): ICommandExecuteManager {
        return mock(ICommandExecuteManager::class.java)
    }

    override fun getGlobalPropertyHolder(): IGlobalPropertyHolder {
        return mock(IGlobalPropertyHolder::class.java)
    }

    override fun getThisSidesCommunicationBootstrap(): ICommunicationBootstrap {
        return mock(ICommunicationBootstrap::class.java)
    }

    override fun getSynchronizedObjectListManager(): ISynchronizedObjectListManager {
        throw UnsupportedOperationException()
    }

    override fun getServiceVersionHandler(): IServiceVersionHandler {
        return mock(IServiceVersionHandler::class.java)
    }

    override fun getLanguageManager(): ILanguageManager {
        return mock(ILanguageManager::class.java)
    }

    override fun getMessageChannelManager(): IMessageChannelManager {
        return mock(IMessageChannelManager::class.java)
    }

    override fun getThisSidesName(): String {
        return ""
    }

    override fun getThisSidesNetworkComponent(): INetworkComponent {
        return mock(INetworkComponent::class.java)
    }

    override fun getThisSidesCloudModule(): ICloudModule {
        return mock(ICloudModule::class.java)
    }
}