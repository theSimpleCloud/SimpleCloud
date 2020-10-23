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

package eu.thesimplecloud.api

import eu.thesimplecloud.api.cachelist.manager.ICacheListManager
import eu.thesimplecloud.api.eventapi.IEventManager
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.api.language.ILanguageManager
import eu.thesimplecloud.api.message.IMessageChannelManager
import eu.thesimplecloud.api.network.component.INetworkComponent
import eu.thesimplecloud.api.player.ICloudPlayerManager
import eu.thesimplecloud.api.screen.ICommandExecutable
import eu.thesimplecloud.api.screen.ICommandExecuteManager
import eu.thesimplecloud.api.service.ICloudServiceManager
import eu.thesimplecloud.api.service.version.IServiceVersionHandler
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroupManager
import eu.thesimplecloud.api.sync.`object`.IGlobalPropertyHolder
import eu.thesimplecloud.api.sync.list.manager.ISynchronizedObjectListManager
import eu.thesimplecloud.api.template.ITemplateManager
import eu.thesimplecloud.api.wrapper.IWrapperManager
import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap

/**
 * The global api for the cloud. It can be accessed from everywhere.
 */
interface ICloudAPI {

    /**
     * Returns the [IWrapperManager] to manage wrapper
     */
    fun getWrapperManager(): IWrapperManager

    /**
     * Returns the [ICloudServiceGroupManager] to manage service groups
     */
    fun getCloudServiceGroupManager(): ICloudServiceGroupManager

    /**
     * Returns the [ICloudServiceManager] to manage services
     */
    fun getCloudServiceManager(): ICloudServiceManager

    /**
     * Returns the [ICloudPlayerManager] to manage players
     */
    fun getCloudPlayerManager(): ICloudPlayerManager

    /**
     * Returns the [IEventManager] to manage and call events
     */
    fun getEventManager(): IEventManager

    /**
     * Returns the [ICommandExecuteManager] used to execute command on [ICommandExecutable]
     */
    fun getCommandExecuteManager(): ICommandExecuteManager

    /**
     * Returns the [ITemplateManager] used to manage the templates
     */
    fun getTemplateManager(): ITemplateManager

    /**
     * Returns the [IGlobalPropertyHolder] used to manage the templates
     */
    fun getGlobalPropertyHolder(): IGlobalPropertyHolder

    /**
     * Returns the [ICommunicationBootstrap] of this side.
     */
    fun getThisSidesCommunicationBootstrap(): ICommunicationBootstrap

    /**
     * Returns the [ISynchronizedObjectListManager]
     */
    fun getSynchronizedObjectListManager(): ISynchronizedObjectListManager

    /**
     * Return the [IMessageChannelManager]
     */
    fun getMessageChannelManager(): IMessageChannelManager

    /**
     * Returns the [ICacheListManager]
     */
    fun getCacheListManager(): ICacheListManager

    /**
     * Returns the [IServiceVersionHandler]
     */
    fun getServiceVersionHandler(): IServiceVersionHandler

    /**
     * Returns the [ILanguageManager]
     */
    fun getLanguageManager(): ILanguageManager

    /**
     * Returns the name of this side
     * e.g Manager / Wrapper-1 / Lobby-1
     */
    fun getThisSidesName(): String

    /**
     * Returns the [INetworkComponent] of this side.
     */
    fun getThisSidesNetworkComponent(): INetworkComponent

    /**
     * Returns whether this side is a manager.
     */
    fun isManager(): Boolean = getThisSidesName() == "Manager"

    /**
     * Returns whether the application is executed on windows.
     */
    fun isWindows(): Boolean = System.getProperty("os.name").toLowerCase().contains("windows")

    /**
     * Returns then cloud module fot this side.
     */
    fun getThisSidesCloudModule(): ICloudModule

}