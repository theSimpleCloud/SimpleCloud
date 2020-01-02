package eu.thesimplecloud.plugin

import eu.thesimplecloud.api.external.ICloudModule

interface ICloudServicePlugin : ICloudModule {

    fun shutdown()

}