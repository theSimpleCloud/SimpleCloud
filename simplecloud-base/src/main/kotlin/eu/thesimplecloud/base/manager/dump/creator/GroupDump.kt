package eu.thesimplecloud.base.manager.dump.creator

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.ServiceType
import eu.thesimplecloud.api.service.version.ServiceVersion
import eu.thesimplecloud.base.manager.dump.DumpFile

/**
 * Created by MrManHD
 * Class create at 30.06.2023 21:03
 */

class GroupDump(
    private val groupName: String,
    private val serviceGroupType: ServiceType,
    private val templateName: String,
    private val maxMemory: Int,
    private val minService: Int,
    private val maxService: Int,
    private val maintenance: Boolean,
    private val static: Boolean,
    private val startPercent: Int,
    private val wrapperName: String,
    private val startPriority: Int,
    private val javaName: String,
    private val permission: String,
    private val serviceVersion: ServiceVersion
) {

    companion object {
        fun getGroupsDump(): List<GroupDump> {
            val cloudServiceGroupManager = CloudAPI.instance.getCloudServiceGroupManager()
            return cloudServiceGroupManager.getAllCachedObjects().map {
                GroupDump(
                    it.getName(),
                    it.getServiceType(),
                    it.getTemplateName(),
                    it.getMaxMemory(),
                    it.getMinimumOnlineServiceCount(),
                    it.getMaximumOnlineServiceCount(),
                    it.isInMaintenance(),
                    it.isStatic(),
                    it.getPercentToStartNewService(),
                    it.getWrapperName() ?: "",
                    it.getStartPriority(),
                    it.getJavaCommandName(),
                    it.getPermission() ?: "null",
                    it.getServiceVersion()
                )
            }
        }

        fun createDumpFile(): String {
            val stringBuilder = StringBuilder()
            getGroupsDump().forEach {
                stringBuilder.append("\n${it.toDumpTxt()}")
            }
            return stringBuilder.toString()
        }
    }

    fun toDumpTxt(): String {
        return DumpFile::class.java.getResource("/dump/group.txt")!!.readText()
            .replace("%GROUP_NAME%", this.groupName)
            .replace("%SERVICE_GROUP_TYPE%", this.serviceGroupType.toString())
            .replace("%TEMPLATE_NAME%", this.templateName)
            .replace("%MAX_MEMORY%", this.maxMemory.toString())
            .replace("%MIN_SERVICE%", this.minService.toString())
            .replace("%MAX_SERVICE%", this.maxService.toString())
            .replace("%MAINTENANCE%", this.maintenance.toString())
            .replace("%STATIC%", this.static.toString())
            .replace("%START_PERCENT%", this.startPercent.toString())
            .replace("%WRAPPER_NAME%", this.wrapperName)
            .replace("%START_PRIORITY%", this.startPriority.toString())
            .replace("%JAVA_NAME%", this.javaName)
            .replace("%PERMISSION%", this.permission)
            .replace("%SERVICE_VERSION%", this.serviceVersion.name)
    }

}