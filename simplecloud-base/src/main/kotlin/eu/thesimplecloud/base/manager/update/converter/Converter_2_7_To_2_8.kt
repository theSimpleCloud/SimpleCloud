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

        val velocityGroupFiles = File(DirectoryPaths.paths.proxyGroupsPath).listFiles()!!
            .filter { JsonLib.fromJsonString(it.readText()).getString("serviceVersion") == "VELOCITY_3" }

        velocityGroupFiles.forEach {
            val jsonLib = JsonLib.fromJsonString(it.readText())
            jsonLib.append("serviceVersion", "VELOCITY")
            jsonLib.saveAsFile(it)
            Launcher.instance.logger.info("Convert ${jsonLib.getString("name")} serviceVersion VELOCITY_3 to VELOCITY")
        }
    }

}