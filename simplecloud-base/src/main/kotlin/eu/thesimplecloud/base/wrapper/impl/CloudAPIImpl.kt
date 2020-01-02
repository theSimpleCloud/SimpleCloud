package eu.thesimplecloud.base.wrapper.impl

import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.ICloudPlayerManager
import eu.thesimplecloud.api.screen.ICommandExecuteManager
import eu.thesimplecloud.api.service.ICloudServiceManager
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroupManager

class CloudAPIImpl : CloudAPI() {

    private val cloudServiceGroupManager = CloudServiceGroupManagerImpl()
    private val cloudServiceManager = CloudServiceManagerImpl()
    private val commandExecuteManager = CommandExecuteManagerImpl()
    private val cloudPlayerManager = CloudPlayerManagerImpl()

    override fun getCloudServiceGroupManager(): ICloudServiceGroupManager = this.cloudServiceGroupManager

    override fun getCloudServiceManger(): ICloudServiceManager = this.cloudServiceManager

    override fun getCloudPlayerManager(): ICloudPlayerManager = this.cloudPlayerManager

    override fun getCommandExecuteManager(): ICommandExecuteManager = this.commandExecuteManager

    override fun getThisSidesName(): String = Wrapper.instance.getThisWrapper().getName()
}