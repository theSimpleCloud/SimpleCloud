package eu.thesimplecloud.base.manager.dump

import eu.thesimplecloud.base.manager.dump.creator.*
import eu.thesimplecloud.base.manager.startup.Manager
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
        .replace("%WRAPPERS%", WrapperDump.createDumpFile())
        .replace("%SERVICES%", ServiceDump.createDumpFile())
        .replace("%TEMPLATES%", TemplateDump.createDumpFile())
        .replace("%GROUPS%", GroupDump.createDumpFile())
        .replace("%STATIC_JARS%", ServiceVersionDump.createDumpFile())
        .replace("%LOG%", getCloudLogs())

    private fun getDumpFileTemplate(): String {
        return DumpFile::class.java.getResource("/dump/dump-file.txt")!!.readText()
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