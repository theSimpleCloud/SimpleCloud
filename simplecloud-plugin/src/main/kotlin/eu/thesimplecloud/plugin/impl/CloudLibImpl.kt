package eu.thesimplecloud.plugin.impl

import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.screen.ICommandExecuteManager
import eu.thesimplecloud.lib.service.ICloudServiceManager
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroupManager

class CloudLibImpl : CloudLib() {

    val cloudServiceManager = CloudServiceManagerImpl()
    val cloudServiceGroupManager = CloudServiceGroupManagerImpl()
    val commandExecuteManagerImpl = CommandExecuteManagerImpl()

    override fun getCloudServiceGroupManager(): ICloudServiceGroupManager = this.cloudServiceGroupManager

    override fun getCloudServiceManger(): ICloudServiceManager = this.cloudServiceManager

    override fun getCommandExecuteManager(): ICommandExecuteManager = this.commandExecuteManagerImpl


}