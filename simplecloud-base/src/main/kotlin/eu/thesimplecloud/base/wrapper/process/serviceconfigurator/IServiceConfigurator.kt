package eu.thesimplecloud.base.wrapper.process.serviceconfigurator

import eu.thesimplecloud.lib.service.ICloudService
import java.io.File

interface IServiceConfigurator {

    /**
     * Edits all necessary files to start a service
     */
    fun configureService(cloudService: ICloudService, serviceTmpDirectory: File)



}