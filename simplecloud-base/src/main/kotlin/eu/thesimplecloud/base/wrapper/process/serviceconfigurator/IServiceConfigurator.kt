package eu.thesimplecloud.base.wrapper.process.serviceconfigurator

import eu.thesimplecloud.launcher.external.ResourceFinder
import eu.thesimplecloud.lib.service.ICloudService
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException

interface IServiceConfigurator {

    /**
     * Edits all necessary files to start a service
     */
    fun configureService(cloudService: ICloudService, serviceTmpDirectory: File)



}