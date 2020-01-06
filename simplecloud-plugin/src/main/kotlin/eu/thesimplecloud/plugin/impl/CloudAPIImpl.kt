package eu.thesimplecloud.plugin.impl

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.eventapi.IEventManager
import eu.thesimplecloud.api.player.ICloudPlayerManager
import eu.thesimplecloud.api.screen.ICommandExecuteManager
import eu.thesimplecloud.api.service.ICloudServiceManager
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroupManager
import eu.thesimplecloud.plugin.startup.CloudPlugin

class CloudAPIImpl : CloudAPI() {

    val cloudServiceManager = CloudServiceManagerImpl()
    val cloudServiceGroupManager = CloudServiceGroupManagerImpl()
    val commandExecuteManagerImpl = CommandExecuteManagerImpl()
    val cloudPlayerManager = CloudPlayerManagerImpl()
    val eventManager = EventManagerImpl()

    override fun getCloudServiceGroupManager(): ICloudServiceGroupManager = this.cloudServiceGroupManager

    override fun getCloudServiceManger(): ICloudServiceManager = this.cloudServiceManager

    override fun getCloudPlayerManager(): ICloudPlayerManager = this.cloudPlayerManager

    override fun getEventManager(): IEventManager = this.eventManager

    override fun getCommandExecuteManager(): ICommandExecuteManager = this.commandExecuteManagerImpl

    override fun getThisSidesName(): String = CloudPlugin.instance.thisServiceName


}