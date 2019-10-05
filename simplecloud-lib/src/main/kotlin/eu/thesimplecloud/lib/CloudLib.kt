package eu.thesimplecloud.lib

import eu.thesimplecloud.lib.wrapper.IWrapperManager
import eu.thesimplecloud.lib.wrapper.impl.DefaultWrapperManager

abstract class CloudLib : ICloudLib {

    init {
        instance = this
    }

    private val wrapperManager: IWrapperManager = DefaultWrapperManager()

    override fun getWrapperManager(): IWrapperManager = this.wrapperManager

    companion object {
        @JvmStatic
        lateinit var instance: CloudLib
    }
}