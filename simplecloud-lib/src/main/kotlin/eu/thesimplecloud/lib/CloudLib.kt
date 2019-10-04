package eu.thesimplecloud.lib

import eu.thesimplecloud.lib.service.ICloudServiceManager
import eu.thesimplecloud.lib.service.impl.DefaultCloudServiceManager
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroupManager
import eu.thesimplecloud.lib.wrapper.IWrapperManager
import eu.thesimplecloud.lib.wrapper.impl.DefaultWrapperManager

abstract class CloudLib : ICloudLib {

    init {
        instance = this
    }

    private val wrapperManager: IWrapperManager = DefaultWrapperManager()
    private val cloudServiceManager: ICloudServiceManager = DefaultCloudServiceManager()

    override fun getWrapperManager(): IWrapperManager = this.wrapperManager

    override fun getCloudServiceGroupManager(): ICloudServiceGroupManager {
    }

    override fun getCloudServiceManger(): ICloudServiceManager = this.cloudServiceManager


    companion object {
        @JvmStatic
        lateinit var instance: CloudLib
    }
}