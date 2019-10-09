package eu.thesimplecloud.base.manager.impl

import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.screen.ICommandExecuteManager
import eu.thesimplecloud.lib.service.ICloudServiceManager
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroupManager

class CloudLibImpl : CloudLib() {

    private val cloudServiceGroupManager = CloudServiceGroupManagerImpl()
    private val cloudServiceManager = CloudServiceManagerImpl()
    private val commandExecuteManager = CommandExecuteManagerImpl()

    override fun getCloudServiceGroupManager(): ICloudServiceGroupManager = this.cloudServiceGroupManager

    override fun getCloudServiceManger(): ICloudServiceManager = this.cloudServiceManager

    override fun getCommandExecuteManager(): ICommandExecuteManager = this.commandExecuteManager
}