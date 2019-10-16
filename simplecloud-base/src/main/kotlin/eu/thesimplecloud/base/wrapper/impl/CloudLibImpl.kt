package eu.thesimplecloud.base.wrapper.impl

import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.screen.ICommandExecuteManager
import eu.thesimplecloud.lib.service.ICloudServiceManager
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroupManager

class CloudLibImpl : CloudLib() {

    override fun getCloudServiceGroupManager(): ICloudServiceGroupManager {
    }

    override fun getCloudServiceManger(): ICloudServiceManager {
    }

    override fun getCommandExecuteManager(): ICommandExecuteManager {
    }
}