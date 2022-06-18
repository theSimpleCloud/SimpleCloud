package eu.thesimplecloud.base.manager.update.converter

import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.jsonlib.JsonLib
import java.io.File

/**
 * Date: 18.06.22
 * Time: 16:44
 * @author Frederick Baier
 *
 */
class Converter_2_3_To_2_4 : IVersionConverter {

    override fun getTargetMinorVersion(): Int {
        return 4
    }

    override fun convert() {
        convertGroupConfigs()
    }

    private fun convertGroupConfigs() {
        convertGroupsInDirectory(File(DirectoryPaths.paths.proxyGroupsPath))
        convertGroupsInDirectory(File(DirectoryPaths.paths.lobbyGroupsPath))
        convertGroupsInDirectory(File(DirectoryPaths.paths.serverGroupsPath))

    }

    private fun convertGroupsInDirectory(dir: File) {
        dir.listFiles()?.forEach {
            convertSingleGroupByFile(it)
        }
    }

    private fun convertSingleGroupByFile(file: File) {
        val groupJsonLib = JsonLib.fromJsonFile(file) ?: return
        if (groupJsonLib.jsonElement.asJsonObject.has("javaCommandName")) {
            return
        }
        groupJsonLib.append("javaCommandName", "java")
        groupJsonLib.saveAsFile(file)
    }

}