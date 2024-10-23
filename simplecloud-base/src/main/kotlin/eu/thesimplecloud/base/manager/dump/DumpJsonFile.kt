package eu.thesimplecloud.base.manager.dump

import eu.thesimplecloud.base.core.jvm.JvmArgument
import eu.thesimplecloud.base.manager.dump.creator.*
import eu.thesimplecloud.base.manager.startup.Manager
import java.io.File

/**
 * Created by MrManHD
 * Class create at 14.11.23 16:55
 */

data class DumpJsonFile(
    val modules: List<String>,
    val jvmArguments: List<JvmArgument>,
    val wrappers: List<WrapperDump>,
    val services: List<ServiceDump>,
    val templates: List<TemplateDump>,
    val groups: List<GroupDump>,
    val staticJars: List<ServiceVersionDump>,
    val logs: String,
) {

    companion object {
        fun createJsonFile(): DumpJsonFile {
            return DumpJsonFile(
                Manager.instance.cloudModuleHandler.getLoadedModules().map { it.getName() },
                Manager.instance.jvmArgumentsConfig.jvmArguments,
                WrapperDump.getWrappersDump(),
                ServiceDump.getServicesDump(),
                TemplateDump.getTemplatesDump(),
                GroupDump.getGroupsDump(),
                ServiceVersionDump.getVersionsDump(),
                File("logs/simplecloud-log.0").readText()
            )
        }
    }

}