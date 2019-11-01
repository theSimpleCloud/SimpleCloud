package eu.thesimplecloud.plugin.startup

import eu.thesimplecloud.plugin.ICloudServicePlugin
import eu.thesimplecloud.plugin.impl.CloudLibImpl


abstract class CloudPlugin(val cloudServicePlugin: ICloudServicePlugin) {

    companion object {
        lateinit var instance: CloudPlugin
    }

    init {
        instance = this
        CloudLibImpl()
    }




}