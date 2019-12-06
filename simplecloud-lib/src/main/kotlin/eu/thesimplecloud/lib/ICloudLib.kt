package eu.thesimplecloud.lib

import eu.thesimplecloud.lib.eventapi.IEventManager
import eu.thesimplecloud.lib.player.ICloudPlayerManager
import eu.thesimplecloud.lib.screen.ICommandExecuteManager
import eu.thesimplecloud.lib.screen.ICommandExecutable
import eu.thesimplecloud.lib.service.ICloudServiceManager
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroupManager
import eu.thesimplecloud.lib.template.ITemplateManager
import eu.thesimplecloud.lib.wrapper.IWrapperManager

/**
 * It represents the main part of a cloud part
 * The Wrapper, Manager and CloudPlugin should implement this interface.
 */
interface ICloudLib {

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
    fun getCloudServiceManger(): ICloudServiceManager

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
     * Returns the name of this side
     * e.g Manager / Wrapper / Lobby-1
     */
    fun getThisSidesName(): String
}