package eu.thesimplecloud.module.support.lib

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.module.support.lib.creator.*
import java.io.File

/**
 * Created by MrManHD
 * Class create at 25.06.2023 20:58
 */

class DumpFile(
    private val time: String
) {

    val fileString = getDumpFileTemplate()
        .replace("%MODULES%", getModulesString())
        .replace("%JVM_ARGUMENTS%", getJvmArgumentsString())
        .replace("%WRAPPERS%", WrapperFileCreator().create())
        .replace("%SERVICES%", ServiceFileCreator().create())
        .replace("%TEMPLATES%", getTemplatesString())
        .replace("%GROUPS%", GroupFileCreator().create())
        .replace("%STATIC_JARS%", ServiceVersionFileCreator().create())
        .replace("%LOG%", getCloudLogs())

    private fun getDumpFileTemplate(): String {
        return DumpFile::class.java.getResource("/temp/dump-file.txt")!!.readText()
            .replace("%TIME%", this.time)
            .replace("%OS_NAME%", System.getProperty("os.name"))
            .replace("%JAVA_VERSION%", System.getProperty("java.version"))
            .replace("%CLOUD_VERSION%", System.getProperty("simplecloud.version"))
    }

    private fun getModulesString(): String {
        val stringBuilder = StringBuilder()
        Manager.instance.cloudModuleHandler.getLoadedModules().forEach {
            stringBuilder.append("\n  ${it.file.name}")
        }
        return stringBuilder.toString()
    }

    private fun getTemplatesString(): String {
        val stringBuilder = StringBuilder()
        CloudAPI.instance.getTemplateManager().getAllCachedObjects().forEach {
            stringBuilder.append("\n  ${it.getName()}")
        }
        return stringBuilder.toString()
    }

    private fun getJvmArgumentsString(): String {
        val stringBuilder = StringBuilder()
        Manager.instance.jvmArgumentsConfig.jvmArguments.forEach {
            stringBuilder.append("\n  ${it.groups.joinToString(", ")}: ${it.arguments.joinToString(" ")}")
        }
        return stringBuilder.toString()
    }

    private fun getCloudLogs(): String {
        return File("logs/simplecloud-log.0").readText()
    }

}