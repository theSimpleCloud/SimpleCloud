package eu.thesimplecloud.base.manager.dump.creator

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.api.service.version.ServiceVersion
import eu.thesimplecloud.base.manager.dump.DumpFile

/**
 * Created by MrManHD
 * Class create at 02.07.2023 14:52
 */

class ServiceDump(
    private val serviceName: String,
    private val groupName: String,
    private val displayName: String,
    private val port: Int,
    private val state: ServiceState,
    private val onlineCount: Int,
    private val memory: Int,
    private val templateName: String,
    private val wrapperName: String,
    private val serviceVersion: ServiceVersion
) {

    companion object {
        fun getServicesDump(): List<ServiceDump> {
            val cloudServiceManager = CloudAPI.instance.getCloudServiceManager()
            return cloudServiceManager.getAllCachedObjects().map {
                ServiceDump(
                    it.getName(),
                    it.getGroupName(),
                    it.getDisplayName(),
                    it.getPort(),
                    it.getState(),
                    it.getOnlineCount(),
                    it.getUsedMemory(),
                    it.getTemplateName(),
                    it.getWrapperName() ?: "",
                    it.getServiceVersion()
                )
            }
        }

        fun createDumpFile(): String {
            val stringBuilder = StringBuilder()
            getServicesDump().forEach {
                stringBuilder.append("\n${it.toDumpTxt()}")
            }
            return stringBuilder.toString()
        }
    }

    fun toDumpTxt(): String {
        return DumpFile::class.java.getResource("/dump/service.txt")!!.readText()
            .replace("%SERVICE_NAME%", this.serviceName)
            .replace("%GROUP_NAME%", this.groupName)
            .replace("%DISPLAY_NAME%", this.displayName)
            .replace("%PORT%", this.port.toString())
            .replace("%STATE%", this.state.toString())
            .replace("%ONLINE_COUNT%", this.onlineCount.toString())
            .replace("%MEMORY%", this.memory.toString())
            .replace("%TEMPLATE_NAME%", this.templateName)
            .replace("%WRAPPER_NAME%", this.wrapperName)
            .replace("%SERVICE_VERSION%", this.serviceVersion.name)
    }

}