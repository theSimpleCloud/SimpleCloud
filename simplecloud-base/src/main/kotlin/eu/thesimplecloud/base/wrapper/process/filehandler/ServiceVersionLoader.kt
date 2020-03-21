package eu.thesimplecloud.base.wrapper.process.filehandler

import eu.thesimplecloud.api.utils.Downloader
import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.api.service.ServiceVersion
import eu.thesimplecloud.api.utils.ZipUtils
import java.io.File

class ServiceVersionLoader {

    @Synchronized
    fun loadVersionFile(serviceVersion: ServiceVersion): File {
        val file = File(DirectoryPaths.paths.minecraftJarsPath + serviceVersion.name + ".jar")
        if (!file.exists()){
            Downloader().userAgentDownload(serviceVersion.downloadLink, file)
            //delete json to prevent bugs in spigot version 1.8
            ZipUtils().deletePath(file, "com/google/gson/")
            Thread.sleep(200)
        }
        return file
    }

}