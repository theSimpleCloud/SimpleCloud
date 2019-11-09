package eu.thesimplecloud.base.wrapper.process.filehandler

import eu.thesimplecloud.lib.utils.Downloader
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import eu.thesimplecloud.lib.service.ServiceVersion
import java.io.File

class ServiceVersionLoader {

    @Synchronized
    fun loadVersionFile(serviceVersion: ServiceVersion): File {
        val file = File(DirectoryPaths.paths.minecraftJarsPath + serviceVersion.name + ".jar")
        if (!file.exists())
            Downloader().userAgentDownload(serviceVersion.downloadLink, file.absolutePath)
        return file
    }

}