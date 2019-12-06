package eu.thesimplecloud.base.manager.impl

import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.player.ICloudPlayerManager
import eu.thesimplecloud.lib.screen.ICommandExecuteManager
import eu.thesimplecloud.lib.service.ICloudServiceManager
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroupManager
import eu.thesimplecloud.lib.template.ITemplateManager
import eu.thesimplecloud.lib.wrapper.IWrapperManager

class CloudLibImpl : CloudLib() {

    private val cloudPlayerManager = CloudPlayerManagerImpl()
    private val cloudServiceGroupManager = CloudServiceGroupManagerImpl()
    private val cloudServiceManager = CloudServiceManagerImpl()
    private val commandExecuteManager = CommandExecuteManagerImpl()
    private val templateManager = TemplateManagerImpl()
    private val wrapperManager = WrapperManagerImpl()


    override fun getCloudServiceGroupManager(): ICloudServiceGroupManager = this.cloudServiceGroupManager

    override fun getCloudServiceManger(): ICloudServiceManager = this.cloudServiceManager

    override fun getCloudPlayerManager(): ICloudPlayerManager = this.cloudPlayerManager

    override fun getCommandExecuteManager(): ICommandExecuteManager = this.commandExecuteManager

    override fun getTemplateManager(): ITemplateManager = this.templateManager

    override fun getWrapperManager(): IWrapperManager = this.wrapperManager

    override fun getThisSidesName(): String = "Manager"


}