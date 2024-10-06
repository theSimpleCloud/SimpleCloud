package eu.thesimplecloud.base.manager.update.converter

import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.launcher.startup.Launcher
import java.io.File

/**
 * @author Niklas Nieberler
 */

class Converter_2_7_To_2_8 : IVersionConverter {

    override fun getTargetMinorVersion(): Int = 8

    override fun convert() {
        val serviceVersionsFile = File(DirectoryPaths.paths.storagePath, "onlineServiceVersions.json")
        serviceVersionsFile.delete()

        changeProxyGroupServiceVersion("VELOCITY", "VELOCITY_1_1_9")
        changeProxyGroupServiceVersion("VELOCITY_3", "VELOCITY_3_4_0")
    }

    private fun changeProxyGroupServiceVersion(from: String, to: String) {
        val groupFiles = File(DirectoryPaths.paths.proxyGroupsPath).listFiles()!!
            .filter { JsonLib.fromJsonString(it.readText()).getString("serviceVersion") == from }

        groupFiles.forEach {
            val jsonLib = JsonLib.fromJsonString(it.readText())
            jsonLib.append("serviceVersion", to)
            jsonLib.saveAsFile(it)
            Launcher.instance.logger.info("Convert ${jsonLib.getString("name")} serviceVersion $from to $to")
        }
    }

}