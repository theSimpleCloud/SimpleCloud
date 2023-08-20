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

package eu.thesimplecloud.base.wrapper.impl

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.eventapi.IEventManager
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.api.language.ILanguageManager
import eu.thesimplecloud.api.language.LanguageManager
import eu.thesimplecloud.api.network.component.INetworkComponent
import eu.thesimplecloud.api.player.ICloudPlayerManager
import eu.thesimplecloud.api.screen.ICommandExecuteManager
import eu.thesimplecloud.api.service.ICloudServiceManager
import eu.thesimplecloud.api.service.version.IServiceVersionHandler
import eu.thesimplecloud.api.service.version.ServiceVersionHandler
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroupManager
import eu.thesimplecloud.api.sync.list.manager.ISynchronizedObjectListManager
import eu.thesimplecloud.api.sync.list.manager.SynchronizedObjectListManager
import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap

class CloudAPIImpl : CloudAPI() {

    private val cloudServiceGroupManager = CloudServiceGroupManagerImpl()
    private val cloudServiceManager = CloudServiceManagerImpl()
    private val commandExecuteManager = CommandExecuteManagerImpl()
    private val cloudPlayerManager = CloudPlayerManagerImpl()
    private val eventManager = EventManagerImpl()
    private val synchronizedObjectListManager = SynchronizedObjectListManager()
    private val serviceVersionHandler = ServiceVersionHandler()
    private val languageManager = LanguageManager()

    init {
        getCacheListManager().registerCacheList(getWrapperManager())
        getCacheListManager().registerCacheList(getCloudServiceManager())
        getCacheListManager().registerCacheList(getCloudServiceGroupManager())
        getCacheListManager().registerCacheList(getTemplateManager())
        getCacheListManager().registerCacheList(getCloudPlayerManager())
    }

    override fun getCloudServiceGroupManager(): ICloudServiceGroupManager = this.cloudServiceGroupManager

    override fun getCloudServiceManager(): ICloudServiceManager = this.cloudServiceManager

    override fun getCloudPlayerManager(): ICloudPlayerManager = this.cloudPlayerManager

    override fun getEventManager(): IEventManager = this.eventManager

    override fun getCommandExecuteManager(): ICommandExecuteManager = this.commandExecuteManager

    override fun getThisSidesCommunicationBootstrap(): ICommunicationBootstrap = Wrapper.instance.communicationClient

    override fun getSynchronizedObjectListManager(): ISynchronizedObjectListManager = this.synchronizedObjectListManager

    override fun getServiceVersionHandler(): IServiceVersionHandler = this.serviceVersionHandler

    override fun getLanguageManager(): ILanguageManager = this.languageManager

    override fun getThisSidesName(): String = Wrapper.instance.thisWrapperName ?: "Wrapper"

    override fun getThisSidesNetworkComponent(): INetworkComponent {
        return Wrapper.instance.getThisWrapper()
    }

    override fun getThisSidesCloudModule(): ICloudModule = Wrapper.instance
}