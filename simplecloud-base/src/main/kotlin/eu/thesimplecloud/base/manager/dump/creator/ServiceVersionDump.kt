package eu.thesimplecloud.base.manager.dump.creator

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.version.type.ServiceAPIType
import eu.thesimplecloud.base.manager.dump.DumpFile

/**
 * Created by MrManHD
 * Class create at 02.07.2023 14:52
 */

class ServiceVersionDump(
    private val jarName: String,
    private val urlName: String,
    private val serviceType: ServiceAPIType,
    private val paperClip: Boolean
) {

    companion object {
        fun getVersionsDump(): List<ServiceVersionDump> {
            val serviceVersionHandler = CloudAPI.instance.getServiceVersionHandler()
            return serviceVersionHandler.getAllVersions().map {
                ServiceVersionDump(
                    it.name,
                    it.downloadURL,
                    it.serviceAPIType,
                    it.isPaperClip
                )
            }
        }

        fun createDumpFile(): String {
            val stringBuilder = StringBuilder()
            getVersionsDump().forEach {
                stringBuilder.append("\n${it.toDumpTxt()}")
            }
            return stringBuilder.toString()
        }
    }

    fun toDumpTxt(): String {
        return DumpFile::class.java.getResource("/dump/static-jars.txt")!!.readText()
            .replace("%JAR_NAME%", this.jarName)
            .replace("%URL_NAME%", this.urlName)
            .replace("%SERVICE_TYPE%", this.serviceType.toString())
            .replace("%PAPER_CLIP%", this.paperClip.toString())
    }

}