package eu.thesimplecloud.module.support.lib.creator

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.module.support.lib.DumpFile

/**
 * Created by MrManHD
 * Class create at 02.07.2023 14:52
 */

class ServiceFileCreator {

    fun create(): String {
        val stringBuilder = StringBuilder()
        CloudAPI.instance.getCloudServiceManager().getAllCachedObjects().forEach {
            stringBuilder.append("\n${getServiceFile(it)}")
        }
        return stringBuilder.toString()
    }

    private fun getServiceFile(service: ICloudService): String {
        return DumpFile::class.java.getResource("/temp/service.txt")!!.readText()
            .replace("%SERVICE_NAME%", service.getName())
            .replace("%GROUP_NAME%", service.getGroupName())
            .replace("%DISPLAY_NAME%", service.getDisplayName())
            .replace("%PORT%", service.getPort().toString())
            .replace("%STATE%", service.getState().toString())
            .replace("%ONLINE_COUNT%", service.getOnlineCount().toString())
            .replace("%MEMORY%", service.getUsedMemory().toString())
            .replace("%TEMPLATE_NAME%", service.getTemplateName())
            .replace("%WRAPPER_NAME%", service.getWrapperName().toString())
            .replace("%SERVICE_VERSION%", service.getServiceVersion().name)
    }

}