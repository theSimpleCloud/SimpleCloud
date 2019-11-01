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

    /**
     * Copies a file outside this jar
     */
    fun copyFileOutOfJar(fileDestination: File, filePathToCopy: String) {
        val stream = this.javaClass.getResourceAsStream(filePathToCopy)
        val parent = fileDestination.parentFile
        parent?.mkdirs()
        if (File(filePathToCopy).exists()) {
            return
        }
        try {
            fileDestination.createNewFile()
            FileUtils.copyInputStreamToFile(stream, fileDestination)
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

    }

}