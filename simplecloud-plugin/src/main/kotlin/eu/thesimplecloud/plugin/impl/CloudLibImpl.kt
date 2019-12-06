package eu.thesimplecloud.plugin.impl

import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.player.ICloudPlayerManager
import eu.thesimplecloud.lib.screen.ICommandExecuteManager
import eu.thesimplecloud.lib.service.ICloudServiceManager
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroupManager
import eu.thesimplecloud.plugin.startup.CloudPlugin

class CloudLibImpl : CloudLib() {

    val cloudServiceManager = CloudServiceManagerImpl()
    val cloudServiceGroupManager = CloudServiceGroupManagerImpl()
    val commandExecuteManagerImpl = CommandExecuteManagerImpl()
    val cloudPlayerManager = CloudPlayerManagerImpl()

    override fun getCloudServiceGroupManager(): ICloudServiceGroupManager = this.cloudServiceGroupManager

    override fun getCloudServiceManger(): ICloudServiceManager = this.cloudServiceManager

    override fun getCloudPlayerManager(): ICloudPlayerManager = this.cloudPlayerManager

    override fun getCommandExecuteManager(): ICommandExecuteManager = this.commandExecuteManagerImpl

    override fun getThisSidesName(): String = CloudPlugin.instance.thisServiceName


}