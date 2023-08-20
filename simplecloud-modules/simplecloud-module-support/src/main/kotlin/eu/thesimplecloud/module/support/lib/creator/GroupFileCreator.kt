package eu.thesimplecloud.module.support.lib.creator

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.module.support.lib.DumpFile

/**
 * Created by MrManHD
 * Class create at 30.06.2023 21:03
 */

class GroupFileCreator {

    fun create(): String {
        val stringBuilder = StringBuilder()
        CloudAPI.instance.getCloudServiceGroupManager().getAllCachedObjects().forEach {
            stringBuilder.append("\n${getGroupFile(it)}")
        }
        return stringBuilder.toString()
    }

    private fun getGroupFile(group: ICloudServiceGroup): String {
        return DumpFile::class.java.getResource("/temp/group.txt")!!.readText()
            .replace("%GROUP_NAME%", group.getName())
            .replace("%SERVICE_GROUP_TYPE%", group.getServiceType().toString())
            .replace("%TEMPLATE_NAME%", group.getTemplateName())
            .replace("%MAX_MEMORY%", group.getMaxMemory().toString())
            .replace("%MIN_SERVICE%", group.getMinimumOnlineServiceCount().toString())
            .replace("%MAX_SERVICE%", group.getMaximumOnlineServiceCount().toString())
            .replace("%MAINTENANCE%", group.isInMaintenance().toString())
            .replace("%STATIC%", group.isStatic().toString())
            .replace("%START_PERCENT%", group.getPercentToStartNewService().toString())
            .replace("%WRAPPER_NAME%", group.getWrapperName().toString())
            .replace("%START_PRIORITY%", group.getStartPriority().toString())
            .replace("%JAVA_NAME%", group.getJavaCommandName())
            .replace("%PERMISSION%", group.getPermission().toString())
            .replace("%SERVICE_VERSION%", group.getServiceVersion().name)
    }

}