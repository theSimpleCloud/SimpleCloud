package eu.thesimplecloud.module.support.lib.creator

import eu.thesimplecloud.api.service.version.ServiceVersion
import eu.thesimplecloud.api.service.version.loader.LocalServiceVersionHandler
import eu.thesimplecloud.module.support.lib.DumpFile

/**
 * Created by MrManHD
 * Class create at 02.07.2023 14:52
 */

class ServiceVersionFileCreator {

    fun create(): String {
        val stringBuilder = StringBuilder()
        LocalServiceVersionHandler().loadVersions().forEach {
            stringBuilder.append("\n${getServiceFile(it)}")
        }
        return stringBuilder.toString()
    }

    private fun getServiceFile(serviceVersion: ServiceVersion): String {
        return DumpFile::class.java.getResource("/temp/static-jars.txt")!!.readText()
            .replace("%JAR_NAME%", serviceVersion.name)
            .replace("%URL_NAME%", serviceVersion.downloadURL)
            .replace("%SERVICE_TYPE%", serviceVersion.serviceAPIType.toString())
            .replace("%PAPER_CLIP%", serviceVersion.isPaperClip.toString())
    }

}